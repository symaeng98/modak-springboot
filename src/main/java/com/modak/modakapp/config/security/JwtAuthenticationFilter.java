package com.modak.modakapp.config.security;

import com.modak.modakapp.domain.enums.ErrorCode;
import com.modak.modakapp.utils.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JwtAuthenticationFilter extends GenericFilterBean {

    private JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Request로 들어오는 Jwt Token의 유효성을 검증 (jwtTokenProvider.validateToken)하는
    // filter를 filterChain에 등록한다.
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            // resolveToken : Request의 Header에서 token 파싱
            String accessToken = jwtUtil.resolveToken((HttpServletRequest) request);

            // validateToken : Jwt 토큰의 유효성 + 만료일자 확인
            Authentication auth = jwtUtil.getAuthentication(accessToken);

            // getAuthentication : Jwt 토큰으로 인증 정보 조회
            SecurityContextHolder.getContext().setAuthentication(auth);
            
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            request.setAttribute("exception", ErrorCode.EXPIRED_TOKEN.getCode());
        } catch (JwtException e) {
            e.printStackTrace();
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN.getCode());
        } catch (NullPointerException e) {
            e.printStackTrace();
            request.setAttribute("exception", ErrorCode.NO_TOKEN.getCode());
        }
        chain.doFilter(request, response);
    }
}
