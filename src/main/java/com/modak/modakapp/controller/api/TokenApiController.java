package com.modak.modakapp.controller.api;

import com.modak.modakapp.DTO.CommonFailResponse;
import com.modak.modakapp.DTO.CommonSuccessResponse;
import com.modak.modakapp.DTO.Token.ReissueTokenResponse;
import com.modak.modakapp.Jwt.TokenService;
import com.modak.modakapp.VO.Member.OpenVO;
import com.modak.modakapp.exception.ExpiredRefreshTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/token")
public class TokenApiController {

    private final TokenService tokenService;

    private final HttpServletResponse servletResponse;

    private final String TOKEN_HEADER = "Bearer ";

    @PostMapping("/reissue")
    public ResponseEntity reissue(@RequestBody OpenVO openVO){
        String accessToken = openVO.getAccessToken().substring(7);
        String refreshToken = openVO.getRefreshToken().substring(7);
        try{
            String newAccessToken = tokenService.reissueToken(accessToken);

            servletResponse.setHeader("ACCESS_TOKEN", TOKEN_HEADER + newAccessToken);
            return ResponseEntity.ok(CommonSuccessResponse.response("AccessToken 재발급",new ReissueTokenResponse("ACCESS_TOKEN")));
        }catch (ExpiredRefreshTokenException e){
            e.printStackTrace();
            return ResponseEntity.ok(CommonFailResponse.response("RefreshToken이 만료되었습니다. 다시 로그인 하세요."));
        }
    }
}
