package com.modak.modakapp.controller;

import com.modak.modakapp.dto.response.CommonFailResponse;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.dto.response.token.ReissueTokenResponse;
import com.modak.modakapp.exception.token.ExpiredRefreshTokenException;
import com.modak.modakapp.exception.token.NotMatchRefreshTokenException;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.utils.jwt.TokenService;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/token")
public class TokenController {
    private final TokenService tokenService;
    private final MemberService memberService;
    private final HttpServletResponse servletResponse;
    private final String TOKEN_HEADER = "Bearer ";

    @ApiResponses({
            @ApiResponse(code = 200, message = "Access Token을 재발급 하였습니다."),
            @ApiResponse(code = 401, message = "RefreshToken이 만료되었습니다. 다시 로그인 하세요.(ExpiredRefreshTokenException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @GetMapping("/reissue")
    public ResponseEntity<?> reissue(
            @RequestHeader(value = "ACCESS_TOKEN") String accessToken,
            @RequestHeader(value = "REFRESH_TOKEN") String refreshToken
    ) {
        String subRefreshToken = tokenService.validateAccessTokenAndRefreshToken(accessToken, refreshToken);
        int memberId = tokenService.getMemberId(subRefreshToken);

        // refresh가 DB에 있는 값과 같은지 확인
        if (!tokenService.isSameRefreshToken(memberService.findMember(memberId), subRefreshToken)) {
            throw new NotMatchRefreshTokenException("회원이 가지고 있는 Refresh Token과 요청한 Refresh Token이 다릅니다.");
        }

        String newAccessToken = tokenService.getAccessToken(memberId);

        servletResponse.setHeader("ACCESS_TOKEN", TOKEN_HEADER + newAccessToken);

        return ResponseEntity.ok(CommonSuccessResponse.response("AccessToken 재발급", new ReissueTokenResponse("ACCESS_TOKEN")));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<?> handleMalformedJwtException(MalformedJwtException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response("JWT 포맷이 올바른지 확인하세요", "MalformedJwtException"));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleSignatureException(SignatureException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response("JWT 포맷이 올바른지 확인하세요", "SignatureException"));
    }

    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ResponseEntity<?> handleExpiredRefreshTokenException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response("RefreshToken이 만료되었습니다. 다시 로그인 하세요.", "ExpiredRefreshTokenException"));
    }

    @ExceptionHandler(NotMatchRefreshTokenException.class)
    public ResponseEntity<?> handleNotMatchRefreshTokenException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response("회원이 가지고 있는 Refresh Token과 요청한 Refresh Token이 다릅니다.", "NotMatchRefreshTokenException"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response(e.getMessage(), e.toString()));
    }
}
