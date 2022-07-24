package com.modak.modakapp.controller.api;

import com.modak.modakapp.DTO.CommonFailResponse;
import com.modak.modakapp.DTO.CommonSuccessResponse;
import com.modak.modakapp.VO.LoginMemberVO;
import com.modak.modakapp.VO.OpenVO;
import com.modak.modakapp.VO.SignUpMemberVO;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Provider;
import com.modak.modakapp.domain.Role;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.exception.ExpiredAccessTokenException;
import com.modak.modakapp.exception.ExpiredRefreshTokenException;
import com.modak.modakapp.exception.NoMemberException;
import com.modak.modakapp.repository.MemberRepository;
import com.modak.modakapp.service.FamilyService;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.Jwt.TokenService;
import com.modak.modakapp.Jwt.JwtUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class MemberApiController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final FamilyService familyService;

    private final TokenService tokenService;

    private final HttpServletResponse servletResponse;
    private final JwtUtil jwtUtil;

    private final String TOKEN_HEADER = "Bearer ";

    @ApiOperation(value = "회원 가입")
    @PostMapping("/member/new")
    public ResponseEntity create(@RequestBody @ApiParam(value = "회원 기본 정보",required = true) SignUpMemberVO signUpMemberVO){
        Family family = new Family();
        family.setName("행복한 우리 가족");
        Long joinFamilyId = familyService.join(family);

        Member member = new Member();

        member.setFamily(family);

        member.setName(signUpMemberVO.getName());

        member.setIs_lunar(signUpMemberVO.getIsLunar());
        // 생일 로직
        // sdfsd
        member.setBirthday(LocalDate.now());

        member.setRole(Role.valueOf(signUpMemberVO.getRole()));

        // Provider
        member.setProvider(Provider.valueOf(signUpMemberVO.getProvider()));

        // ProviderId
        member.setProviderId(signUpMemberVO.getProviderId());

        // chatLastJoined
        member.setChatLastJoined(LocalDateTime.now());

        // chatNowJoining
        member.setChatNowJoining(0);

        // Refresh Token
        member.setRefreshToken("refresh");

        // FCM Token
        member.setFcmToken("FCM임ㅋ");

        // 저장
        Long memberId = memberService.join(member);

        // 회원 생성이 완료된 경우
        String accessToken = tokenService.getAccessToken(memberId);
        String refreshToken = tokenService.getRefreshToken(memberId);
        memberService.updateRefreshToken(memberId,refreshToken);

        servletResponse.setHeader("ACCESS_TOKEN", TOKEN_HEADER + accessToken);
        servletResponse.setHeader("REFRESH_TOKEN", TOKEN_HEADER + refreshToken);
        log.info("woi 저장 완료");

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response("회원 생성 완료",new CreateMemberResponse(memberId,joinFamilyId)));
    }


    @PostMapping("/member/login")
    public ResponseEntity<?> login(@RequestBody @ApiParam(value = "Provider, ProviderId",required = true) LoginMemberVO loginMemberVO){
        String providerId = loginMemberVO.getProviderId();
        try{
            Member findMember = memberService.findMemberByProviderId(providerId);
            Long memberId = findMember.getId();
            String newRefreshToken = tokenService.getRefreshToken(memberId);
            String newAccessToken = tokenService.getAccessToken(memberId);
            memberService.updateRefreshToken(memberId,newRefreshToken);
            servletResponse.setHeader("ACCESS_TOKEN", TOKEN_HEADER + newAccessToken);
            servletResponse.setHeader("REFRESH_TOKEN", TOKEN_HEADER + newRefreshToken);

            return ResponseEntity.ok(CommonSuccessResponse.response("로그인 성공", new LoginMemberResponse(memberId)));
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.ok(CommonFailResponse.response("로그인 실패"));
    }

    /**
     *
     * @param openVO accessToken, refreshToken
     * @return accessToken
     * accessToken이 만료되었다는 가정 하에 refreshToken이 만료되지 않았다면 accessToken 발급
     */
    @ApiResponses({
            @ApiResponse(code=200, message = "서버는 잘 동작했음 status false면 넘겨주는 값 확인"),
            @ApiResponse(code=401, message = "Refresh 토큰 만료됨, 재로그인"),
            })
    @PostMapping("/member/reissue")
    public ResponseEntity reissue(@RequestBody @ApiParam(value = "가지고 있는 Access 토큰과 Refresh 토큰",required = true) OpenVO openVO){
        String accessToken = openVO.getAccessToken();
        try{
            if(!tokenService.isAccessTokenExpired(accessToken)){
                return ResponseEntity.ok(CommonFailResponse.response("아직 사용 가능"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        try {
            String newAccessToken = tokenService.reissueToken(accessToken);
            servletResponse.setHeader("ACCESS_TOKEN", TOKEN_HEADER + newAccessToken);
            return ResponseEntity.ok(CommonSuccessResponse.response("토큰 재발급 성공", new ReissueTokenResponse("ACCESS_TOKEN")));
        } catch (ExpiredRefreshTokenException e) {
            // 에러 처리 로직
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response("리프레시 토큰이 유효하지 않습니다. 다시 로그인하세요."));
    }
    @Data
    @AllArgsConstructor
    static class CreateMemberResponse {
        private Long memberId;
        private Long familyId;
    }

    @Data
    @AllArgsConstructor
    static class LoginMemberResponse {
        private Long memberId;
    }

    @Data
    @AllArgsConstructor
    static class ReissueTokenResponse {
        private String type;
    }
}