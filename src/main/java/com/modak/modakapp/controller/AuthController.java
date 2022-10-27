package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Anniversary;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.TodayFortune;
import com.modak.modakapp.domain.enums.Category;
import com.modak.modakapp.domain.enums.Provider;
import com.modak.modakapp.domain.enums.Role;
import com.modak.modakapp.dto.member.MemberAndFamilyMemberDTO;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.exception.member.MemberAlreadyExistsException;
import com.modak.modakapp.exception.token.NotMatchRefreshTokenException;
import com.modak.modakapp.service.AnniversaryService;
import com.modak.modakapp.service.FamilyService;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.service.TodayFortuneService;
import com.modak.modakapp.utils.jwt.TokenService;
import com.modak.modakapp.vo.member.SignUpMemberVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auth")
@Slf4j
public class AuthController {
    private final MemberService memberService;
    private final FamilyService familyService;
    private final AnniversaryService anniversaryService;
    private final TokenService tokenService;
    private final TodayFortuneService todayFortuneService;
    private final HttpServletResponse servletResponse;
    private final String TOKEN_HEADER = "Bearer ";
    private final String ACCESS_TOKEN = "Access-Token";
    private final String REFRESH_TOKEN = "Refresh-Token";

    @ApiResponses({
            @ApiResponse(code = 201, message = "성공적으로 회원 가입을 마쳤습니다."),
            @ApiResponse(code = 409, message = "이미 가입된 회원입니다.(MemberAlreadyExistsException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요."),
    })
    @ApiOperation(value = "회원 가입")
    @PostMapping()
    public ResponseEntity<CommonSuccessResponse<MemberAndFamilyMemberDTO>> createMember(
            @RequestBody @Valid SignUpMemberVO signUpMemberVO
    ) {
        if (memberService.isMemberExists(signUpMemberVO.getProviderId())) {
            throw new MemberAlreadyExistsException("이미 존재하는 회원입니다.");
        }

        String invitationCode = familyService.generateInvitationCode();
        Family family = Family.builder().name("행복한 우리 가족").code(invitationCode).build();
        familyService.join(family);

        Date birthday = Date.valueOf(signUpMemberVO.getBirthday());
        String colorForMember = memberService.getColorForMember(family);

        TodayFortune todayFortune = todayFortuneService.generateTodayFortune();

        // 회원 등록
        Member member = Member.builder()
                .family(family)
                .name(signUpMemberVO.getName())
                .isLunar(signUpMemberVO.getIsLunar())
                .birthday(birthday)
                .role(Role.valueOf(signUpMemberVO.getRole()))
                .color(colorForMember)
                .provider(Provider.valueOf(signUpMemberVO.getProvider()))
                .providerId(signUpMemberVO.getProviderId())
                .chatLastJoined(Timestamp.valueOf(LocalDateTime.now()))
                .refreshToken("default refresh")
                .fcmToken("default fcm")
                .roles(Collections.singletonList("ROLE_USER"))
                .todayFortune(todayFortune)
                .build();

        int memberId = memberService.join(member);

        // 생일 생성
        Anniversary anniversary = Anniversary.builder()
                .member(member)
                .family(family)
                .category(Category.CON)
                .isYear(1)
                .title(member.getName() + " 생일")
                .isBirthday(1)
                .isLunar(member.getIsLunar())
                .startDate(birthday)
                .endDate(birthday)
                .build();

        anniversaryService.join(anniversary);

        String accessToken = tokenService.getAccessToken(memberId);
        String refreshToken = tokenService.getRefreshToken(memberId);
        memberService.updateRefreshToken(member, refreshToken);

        servletResponse.setHeader(ACCESS_TOKEN, TOKEN_HEADER + accessToken);
        servletResponse.setHeader(REFRESH_TOKEN, TOKEN_HEADER + refreshToken);

        MemberAndFamilyMemberDTO memberAndFamilyMemberDTO = MemberAndFamilyMemberDTO.builder()
                .familyCode(family.getCode())
                .memberResult(memberService.getMemberInfo(member))
                .familyMembersResult(memberService.getFamilyMembersInfo(member))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonSuccessResponse<>("회원 가입 성공", memberAndFamilyMemberDTO, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 로그인을 완료했습니다."),
            @ApiResponse(code = 404, message = "회원 정보가 없습니다. 회원 가입 페이지로 이동하세요.(NoSuchMemberException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요."),
    })
    @ApiOperation(value = "소셜 로그인")
    @GetMapping("/login/social")
    public ResponseEntity<CommonSuccessResponse<MemberAndFamilyMemberDTO>> socialLogin(
            @RequestHeader(value = "Provider") String provider,
            @RequestHeader(value = "Provider-Id") String providerId
    ) {
        Member member = memberService.getMemberByProviderAndProviderId(Provider.valueOf(provider), providerId);

        Family family = member.getFamily();
        int memberId = member.getId();

        String newRefreshToken = tokenService.getRefreshToken(memberId);
        String newAccessToken = tokenService.getAccessToken(memberId);
        memberService.updateRefreshToken(member, newRefreshToken);

        servletResponse.setHeader(ACCESS_TOKEN, TOKEN_HEADER + newAccessToken);
        servletResponse.setHeader(REFRESH_TOKEN, TOKEN_HEADER + newRefreshToken);

        MemberAndFamilyMemberDTO memberAndFamilyMemberDTO = MemberAndFamilyMemberDTO.builder()
                .familyCode(family.getCode())
                .memberResult(memberService.getMemberInfo(member))
                .familyMembersResult(memberService.getFamilyMembersInfo(member))
                .build();

        return ResponseEntity.ok(new CommonSuccessResponse<>("소셜 로그인 성공", memberAndFamilyMemberDTO, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "토큰 재발급을 성공했습니다."),
            @ApiResponse(code = 401, message = "Refresh Token 정보가 데이터베이스의 정보와 다릅니다.(NotMatchRefreshTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요."),
    })
    @ApiOperation(value = "토큰 로그인")
    @GetMapping("/login/token")
    public ResponseEntity<CommonSuccessResponse<MemberAndFamilyMemberDTO>> tokenLogin(
            @RequestHeader(value = REFRESH_TOKEN) String refreshToken
    ) {
        String findRefreshToken = tokenService.validateRefreshTokenExpired(refreshToken);

        int memberId = tokenService.getMemberId(refreshToken.substring(7));

        if (!tokenService.isSameRefreshToken(memberService.getMember(memberId), findRefreshToken)) {
            throw new NotMatchRefreshTokenException("회원이 가지고 있는 Refresh Token과 요청한 Refresh Token이 다릅니다.");
        }

        String newAccessToken = tokenService.getAccessToken(memberId);
        String newRefreshToken = tokenService.getRefreshToken(memberId);

        Member member = memberService.getMemberWithFamily(memberId);
        Family family = member.getFamily();

        memberService.updateRefreshToken(member, newRefreshToken);

        servletResponse.setHeader(ACCESS_TOKEN, TOKEN_HEADER + newAccessToken);
        servletResponse.setHeader(REFRESH_TOKEN, TOKEN_HEADER + newRefreshToken);

        MemberAndFamilyMemberDTO memberAndFamilyMemberDTO = MemberAndFamilyMemberDTO.builder()
                .familyCode(family.getCode())
                .memberResult(memberService.getMemberInfo(member))
                .familyMembersResult(memberService.getFamilyMembersInfo(member))
                .build();

        return ResponseEntity.ok(new CommonSuccessResponse<>("Access Token, Refresh Token 발급 성공", memberAndFamilyMemberDTO, true));
    }
}

