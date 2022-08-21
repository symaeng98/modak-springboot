package com.modak.modakapp.controller;

import com.modak.modakapp.dto.response.CommonFailResponse;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.dto.response.token.ReissueTokenResponse;
import com.modak.modakapp.exception.token.ExpiredRefreshTokenException;
import com.modak.modakapp.utils.jwt.TokenService;
import com.modak.modakapp.vo.member.TokenVO;
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
    private final HttpServletResponse servletResponse;
    private final String TOKEN_HEADER = "Bearer ";

    @ApiResponses({
            @ApiResponse(code = 200, message = "Access Token을 재발급 하였습니다."),
            @ApiResponse(code = 401, message = "RefreshToken이 만료되었습니다. 다시 로그인 하세요.(ExpiredRefreshTokenException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @GetMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestHeader TokenVO tokenVO) {
        String accessToken = tokenVO.getAccessToken().substring(7);
        String refreshToken = tokenVO.getRefreshToken().substring(7);

        String newAccessToken = tokenService.reissueToken(accessToken);

        servletResponse.setHeader("ACCESS_TOKEN", TOKEN_HEADER + newAccessToken);

        return ResponseEntity.ok(CommonSuccessResponse.response("AccessToken 재발급", new ReissueTokenResponse("ACCESS_TOKEN")));
    }

    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ResponseEntity<?> handleExpiredRefreshTokenException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response("RefreshToken이 만료되었습니다. 다시 로그인 하세요.", "ExpiredRefreshTokenException"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response(e.getMessage(), e.toString()));
    }
}
