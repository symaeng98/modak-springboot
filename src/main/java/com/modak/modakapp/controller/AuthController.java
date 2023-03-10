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
            @ApiResponse(code = 201, message = "??????????????? ?????? ????????? ???????????????."),
            @ApiResponse(code = 409, message = "?????? ????????? ???????????????.(MemberAlreadyExistsException)"),
            @ApiResponse(code = 400, message = "?????? ???????????? ???????????????."),
    })
    @ApiOperation(value = "?????? ??????")
    @PostMapping()
    public ResponseEntity<CommonSuccessResponse<MemberAndFamilyMemberDTO>> createMember(
            @RequestBody @Valid SignUpMemberVO signUpMemberVO
    ) {
        if (memberService.isMemberExists(signUpMemberVO.getProviderId())) {
            throw new MemberAlreadyExistsException("?????? ???????????? ???????????????.");
        }

        String invitationCode = familyService.generateInvitationCode();
        Family family = Family.builder().name("????????? ?????? ??????").code(invitationCode).build();
        familyService.join(family);

        Date birthday = Date.valueOf(signUpMemberVO.getBirthday());
        String colorForMember = memberService.getColorForMember(family);

        TodayFortune todayFortune = todayFortuneService.generateTodayFortune();

        // ?????? ??????
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

        // ?????? ??????
        Anniversary anniversary = Anniversary.builder()
                .member(member)
                .family(family)
                .category(Category.CON)
                .isYear(1)
                .title(member.getName() + " ??????")
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

        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonSuccessResponse<>("?????? ?????? ??????", memberAndFamilyMemberDTO, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "??????????????? ???????????? ??????????????????."),
            @ApiResponse(code = 404, message = "?????? ????????? ????????????. ?????? ?????? ???????????? ???????????????.(NoSuchMemberException)"),
            @ApiResponse(code = 400, message = "?????? ???????????? ???????????????."),
    })
    @ApiOperation(value = "?????? ?????????")
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

        return ResponseEntity.ok(new CommonSuccessResponse<>("?????? ????????? ??????", memberAndFamilyMemberDTO, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "?????? ???????????? ??????????????????."),
            @ApiResponse(code = 401, message = "Refresh Token ????????? ????????????????????? ????????? ????????????.(NotMatchRefreshTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT ????????? ???????????? ???????????????.(MalformedJwtException)\n2. JWT ????????? ???????????? ???????????????.(SignatureException)\n3. ?????? ???????????? ???????????????."),
    })
    @ApiOperation(value = "?????? ?????????")
    @GetMapping("/login/token")
    public ResponseEntity<CommonSuccessResponse<MemberAndFamilyMemberDTO>> tokenLogin(
            @RequestHeader(value = REFRESH_TOKEN) String refreshToken
    ) {
        String findRefreshToken = tokenService.validateRefreshTokenExpired(refreshToken);

        int memberId = tokenService.getMemberId(refreshToken.substring(7));

        if (!tokenService.isSameRefreshToken(memberService.getMember(memberId), findRefreshToken)) {
            throw new NotMatchRefreshTokenException("????????? ????????? ?????? Refresh Token??? ????????? Refresh Token??? ????????????.");
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

        return ResponseEntity.ok(new CommonSuccessResponse<>("Access Token, Refresh Token ?????? ??????", memberAndFamilyMemberDTO, true));
    }
}

