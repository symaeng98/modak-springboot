package com.modak.modakapp.service;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.repository.MemberRepository;
import com.modak.modakapp.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl{
    private final JwtUtil jwtUtil;
//    private final RedisUtil redisUtil;
    private final MemberService memberService;

    /**
     * JWT AccessToken과 RefreshToken을 발급한다.
     *
     * @param memberId 회원 아이디
     * @return accessToken String 타입의 엑세스 토큰
     */

    public String getRefreshToken(String memberId) {
        return jwtUtil.generateRefreshToken(memberId);
    }

    public String getAccessToken(String memberId) {
        return jwtUtil.generateAccessToken(memberId);
    }


    /**
     * JWT AccessToken 만료시 RefreshToken 유효 여부를 판단 후 재 발급한다.
     *
     * @param accessToken 엑세스 토큰
     * @return accessToken을 재발급
     */
    public String reissueToken(String accessToken) {
        // 1. 회원 아이디 파싱
        String memberId = jwtUtil.getMemberId(accessToken);

        Member findMember = memberService.findMember(Long.parseLong(memberId));

        // 3 Refresh Token 유효 여부 판단
        if(jwtUtil.isTokenExpired(findMember.getRefreshToken())) {
//            throw new ExpiredRefreshTokenException("재 로그인이 필요합니다.", NbbangException.EXPIRED_REFRESH_TOKEN);
            System.out.println("로그인 다시 해야됨!");
            throw new IllegalStateException();
        }

        // 4. 회원 아이디로 새로운 토큰 생성
        return getAccessToken(memberId);
    }
}