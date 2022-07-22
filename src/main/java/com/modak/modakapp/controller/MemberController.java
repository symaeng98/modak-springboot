package com.modak.modakapp.controller;

import com.modak.modakapp.VO.LoginMemberVO;
import com.modak.modakapp.VO.OpenVO;
import com.modak.modakapp.VO.SignUpMemberVO;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Provider;
import com.modak.modakapp.domain.Role;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.repository.MemberRepository;
import com.modak.modakapp.service.FamilyService;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.service.TokenServiceImpl;
import com.modak.modakapp.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final FamilyService familyService;

    private final TokenServiceImpl tokenService;

    private final HttpServletResponse servletResponse;
    private final JwtUtil jwtUtil;


    @PostMapping("/new")
    public String create(@RequestBody SignUpMemberVO signUpMemberVO){
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
        Member findMember = memberRepository.findOne(memberId);

        // 회원 생성이 완료된 경우
        String accessToken = tokenService.getAccessToken(memberId.toString());
        String refreshToken = tokenService.getRefreshToken(memberId.toString());

        findMember.setRefreshToken(refreshToken);
        memberService.join(member);

        servletResponse.setHeader("ACCESS_TOKEN", "Bearer " + accessToken);
        servletResponse.setHeader("REFRESH_TOKEN", "Bearer " + refreshToken);
        log.info("woi 저장 완료");

        return "woi";
    }

//    @PostMapping("/new")
//    public ResponseEntity<?> signUp(@RequestBody LoginMemberVO request, HttpServletResponse servletResponse) {
////        if(request.getMemberId().length() < 1) {
////            throw new NoCreateMemberException("잘못된 회원아이디입니다.", NbbangException.NO_CREATE_MEMBER);
////        }
////        // 요청 데이터 엔티티에 저장
////        MemberDTO savedMember = memberService.saveMember(MemberRegisterRequest.toEntity(request), request.getOttId(), request.getRecommendMemberId());
//
//        // 회원 생성이 완료된 경우
//        String accessToken = tokenService.manageToken(savedMember.getMemberId(), savedMember.getNickname());
//        servletResponse.setHeader("Authorization", "Bearer " + accessToken);
//        log.info("redis 저장 완료");
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(CommonSuccessResponse.response(true, MemberLoginInfoResponse.create(savedMember), "회원가입에 성공했습니다."));
//
//    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginMemberVO loginMemberVO){
        String providerId = loginMemberVO.getProviderId();
        Member findMember = memberService.findMemberByProviderId(providerId);
        if(findMember!=null){
            System.out.println("oh yeah");
        }
        else{
            System.out.println("nonononononono");
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/reissue")
    public ResponseEntity reissue(@RequestBody OpenVO openVO){
        String accessToken = openVO.getAccessToken();

        if(!jwtUtil.isTokenExpired(accessToken)){
            System.out.println("로그인 성공");
            return ResponseEntity.ok(HttpStatus.OK);
        }

        String newAccessToken = tokenService.reissueToken(accessToken);
        servletResponse.setHeader("ACCESS_TOKEN", "Bearer " + newAccessToken);
        return ResponseEntity.ok("토큰 발급");
    }

}