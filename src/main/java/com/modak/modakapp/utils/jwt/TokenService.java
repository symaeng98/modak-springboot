package com.modak.modakapp.utils.jwt;

import com.modak.modakapp.domain.Member;
import com.modak.modakapp.exception.token.ExpiredAccessTokenException;
import com.modak.modakapp.exception.token.ExpiredRefreshTokenException;
import com.modak.modakapp.service.MemberService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtUtil jwtUtil;
    //    private final RedisUtil redisUtil;
    private final MemberService memberService;

    /**
     * JWT AccessToken과 RefreshToken을 발급한다.
     *
     * @param memberId 회원 아이디
     * @return accessToken String 타입의 엑세스 토큰
     */

    public String getRefreshToken(int memberId) {
        return jwtUtil.generateRefreshToken(memberId);
    }

    public String getAccessToken(int memberId) {
        return jwtUtil.generateAccessToken(memberId);
    }


    /**
     * JWT AccessToken 만료시 RefreshToken 유효 여부를 판단 후 재발급한다.
     *
     * @param accessToken 엑세스 토큰
     * @return accessToken을 재발급
     */
    public String reissueToken(String accessToken) {
        // 1. 회원 아이디 파싱
        int memberId = jwtUtil.getMemberId(accessToken);
        Member findMember = memberService.findMember(memberId);
        // 2. Refresh Token 유효 여부 판F단
        if (jwtUtil.isTokenExpired(findMember.getRefreshToken())) {
            System.out.println("로그인 다시 해야됨!");
            throw new ExpiredRefreshTokenException("재 로그인이 필요합니다!");
        }
        // 3. 회원 아이디로 새로운 토큰 생성
        return getAccessToken(memberId);
    }

    public void validateAccessTokenExpired(String accessToken) {
        try {
            jwtUtil.isTokenExpired(accessToken);
        } catch (ExpiredJwtException e) {
            throw new ExpiredAccessTokenException("AccessToken이 만료되었습니다.");
        }
    }

    public void validateRefreshTokenExpired(String refreshToken) {
        try {
            jwtUtil.isTokenExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new ExpiredRefreshTokenException("RefreshToken이 만료되었습니다.");
        }
    }

    public int getMemberId(String token) {
        return jwtUtil.getMemberId(token);
    }

    // access와 refresh가 이상한 값인지 확인 후 refresh 토큰 반환
    public String validateToken(String accessToken, String refreshToken) {
        String at = accessToken.substring(7);
        String rt = refreshToken.substring(7);
        validateRefreshTokenExpired(rt);
        return rt;
    }

    public boolean isSameRefreshToken(Member member, String refreshToken) {
        return member.getRefreshToken().equals(refreshToken);
    }
}