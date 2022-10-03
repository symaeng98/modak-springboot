package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Anniversary;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.dto.member.MemberAndFamilyMemberDTO;
import com.modak.modakapp.dto.member.MemberDTO;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.service.AnniversaryService;
import com.modak.modakapp.service.FamilyService;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.utils.jwt.TokenService;
import com.modak.modakapp.vo.member.InvitationVO;
import com.modak.modakapp.vo.member.info.UpdateMemberFamilyNameVO;
import com.modak.modakapp.vo.member.info.UpdateMemberTagVO;
import com.modak.modakapp.vo.member.info.UpdateMemberVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/member")
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final FamilyService familyService;
    private final AnniversaryService anniversaryService;
    private final TokenService tokenService;
    private final String ACCESS_TOKEN = "Access-Token";

//    @ApiResponses({
//            @ApiResponse(code = 201, message = "성공적으로 회원 가입을 마쳤습니다."),
//            @ApiResponse(code = 409, message = "이미 가입된 회원입니다.(MemberAlreadyExistsException)"),
//            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
//    })
//    @ApiOperation(value = "회원 가입")
//    @PostMapping()
//    public ResponseEntity<CommonSuccessResponse<MemberAndFamilyMemberDTO>> createMember(@RequestBody SignUpMemberVO signUpMemberVO) {
//        if (memberService.isMemberExists(signUpMemberVO.getProviderId())) {
//            throw new MemberAlreadyExistsException("이미 존재하는 회원입니다.");
//        }
//
//        String invitationCode = familyService.generateInvitationCode();
//        Family family = Family.builder().name("행복한 우리 가족").code(invitationCode).build();
//        familyService.join(family);
//
//        Date birthday = Date.valueOf(signUpMemberVO.getBirthday());
//        String colorForMember = memberService.getColorForMember(family);
//
//        // 회원 등록
//        Member member = Member.builder()
//                .family(family)
//                .name(signUpMemberVO.getName())
//                .isLunar(signUpMemberVO.getIsLunar())
//                .birthday(birthday)
//                .role(Role.valueOf(signUpMemberVO.getRole()))
//                .color(colorForMember)
//                .provider(Provider.valueOf(signUpMemberVO.getProvider()))
//                .providerId(signUpMemberVO.getProviderId())
//                .chatLastJoined(Timestamp.valueOf(LocalDateTime.now()))
//                .refreshToken("default refresh")
//                .fcmToken("default fcm")
//                .build();
//
//        int memberId = memberService.join(member);
//
//        // 생일 생성
//        Anniversary anniversary = Anniversary.builder()
//                .member(member)
//                .family(family)
//                .category(Category.CON)
//                .isYear(1)
//                .title(member.getName() + " 생일")
//                .isBirthday(1)
//                .isLunar(signUpMemberVO.getIsLunar())
//                .startDate(birthday)
//                .endDate(birthday)
//                .build();
//
//        anniversaryService.join(anniversary);
//
//        String accessToken = tokenService.getAccessToken(memberId);
//        String refreshToken = tokenService.getRefreshToken(memberId);
//        memberService.updateRefreshToken(member, refreshToken);
//
//        servletResponse.setHeader(ACCESS_TOKEN, TOKEN_HEADER + accessToken);
//        servletResponse.setHeader(REFRESH_TOKEN, TOKEN_HEADER + refreshToken);
//
//        MemberAndFamilyMemberDTO memberAndFamilyMemberDTO = new MemberAndFamilyMemberDTO(family.getCode(), memberService.getMemberInfo(member), memberService.getFamilyMembersInfo(member));
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonSuccessResponse<>("회원 가입 성공", memberAndFamilyMemberDTO, true));
//    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 초대 받았습니다."),
            @ApiResponse(code = 404, message = "회원 정보가 없습니다. 회원 가입 페이지로 이동하세요.(NoSuchMemberException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @ApiOperation(value = "초대코드로 해당 가족에 포함하기")
    @PutMapping("/invitations")
    public ResponseEntity<CommonSuccessResponse<MemberAndFamilyMemberDTO>> invite(
            @RequestBody InvitationVO invitationVO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Family family = familyService.getByCode(invitationVO.getInvitationCode());
        Member member = memberService.getMember(memberId);

        String colorForMember = memberService.getColorForMember(family);
        memberService.updateMemberFamily(member, family, colorForMember);


        MemberAndFamilyMemberDTO memberAndFamilyMemberDTO = new MemberAndFamilyMemberDTO(family.getCode(), memberService.getMemberInfo(member), memberService.getFamilyMembersInfo(member));

        return ResponseEntity.ok(new CommonSuccessResponse<>("회원 초대 받기 성공", memberAndFamilyMemberDTO, true));
    }

//    @ApiResponses({
//            @ApiResponse(code = 200, message = "성공적으로 로그인을 완료했습니다."),
//            @ApiResponse(code = 404, message = "회원 정보가 없습니다. 회원 가입 페이지로 이동하세요.(NoSuchMemberException)"),
//            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
//    })
//    @ApiOperation(value = "소셜 로그인 버튼 클릭시 호출")
//    @GetMapping("/login/social")
//    public ResponseEntity<CommonSuccessResponse<MemberAndFamilyMemberDTO>> socialLogin(
//            @RequestHeader(value = "Provider") String provider,
//            @RequestHeader(value = "Provider-Id") String providerId
//    ) {
//        System.out.println(LocalDateTime.now());
//        Member member = memberService.getMemberByProviderAndProviderId(Provider.valueOf(provider), providerId);
//        Family family = member.getFamily();
//        int memberId = member.getId();
//
//        String newRefreshToken = tokenService.getRefreshToken(memberId);
//        String newAccessToken = tokenService.getAccessToken(memberId);
//        memberService.updateRefreshToken(member, newRefreshToken);
//
//        servletResponse.setHeader(ACCESS_TOKEN, TOKEN_HEADER + newAccessToken);
//        servletResponse.setHeader(REFRESH_TOKEN, TOKEN_HEADER + newRefreshToken);
//
//        MemberAndFamilyMemberDTO memberAndFamilyMemberDTO = new MemberAndFamilyMemberDTO(family.getCode(), memberService.getMemberInfo(member), memberService.getFamilyMembersInfo(member));
//
//        return ResponseEntity.ok(new CommonSuccessResponse<>("소셜 로그인 성공", memberAndFamilyMemberDTO, true));
//    }
//
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "토큰 재발급을 성공했습니다."),
//            @ApiResponse(code = 401, message = "1. 만료된 Access Token 입니다.(ExpiredAccessTokenException)\n2. Refresh Token 정보가 데이터베이스의 정보와 다릅니다.(NotMatchRefreshTokenException)"),
//            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
//    })
//    @ApiOperation(value = "토큰 로그인")
//    @GetMapping("{member_id}/login/token")
//    public ResponseEntity<CommonSuccessResponse<MemberAndFamilyMemberDTO>> tokenLogin(
//            @RequestHeader(value = REFRESH_TOKEN) String refreshToken,
//            @PathVariable("member_id") int memberId
//    ) {
//        String findRefreshToken = tokenService.validateRefreshTokenExpired(refreshToken);
//
//        if (!tokenService.isSameRefreshToken(memberService.getMember(memberId), findRefreshToken)) {
//            throw new NotMatchRefreshTokenException("회원이 가지고 있는 Refresh Token과 요청한 Refresh Token이 다릅니다.");
//        }
//
//        String newAccessToken = tokenService.getAccessToken(memberId);
//        String newRefreshToken = tokenService.getRefreshToken(memberId);
//
//        Member member = memberService.getMemberWithFamily(memberId);
//        Family family = member.getFamily();
//
//        memberService.updateRefreshToken(member, newRefreshToken);
//
//        servletResponse.setHeader(ACCESS_TOKEN, TOKEN_HEADER + newAccessToken);
//        servletResponse.setHeader(REFRESH_TOKEN, TOKEN_HEADER + newRefreshToken);
//
//        MemberAndFamilyMemberDTO memberAndFamilyMemberDto = new MemberAndFamilyMemberDTO(family.getCode(), memberService.getMemberInfo(member), memberService.getFamilyMembersInfo(member));
//
//        return ResponseEntity.ok(new CommonSuccessResponse<>("Access Token, Refresh Token 발급 성공", memberAndFamilyMemberDto, true));
//    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 정보를 수정하였습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Access Token 입니다.(ExpiredAccessTokenException)"),
    })
    @ApiOperation(value = "유저 개인 정보 변경")
    @PutMapping()
    public ResponseEntity<CommonSuccessResponse<MemberDTO>> updateMember(
            @RequestBody UpdateMemberVO updateMemberVO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member member = memberService.getMember(memberId);

        memberService.updateMember(member, updateMemberVO);

        Anniversary anniversary = anniversaryService.getBirthdayByMember(memberId);
        anniversaryService.updateBirthday(anniversary.getId(), updateMemberVO.getBirthday(), updateMemberVO.getIsLunar());

        MemberDTO memberInfo = memberService.getMemberInfo(member);

        return ResponseEntity.ok(new CommonSuccessResponse<>("회원 개인 정보 변경 성공", memberInfo, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 정보 가져오기를 성공했습니다.."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Access Token 입니다.(ExpiredAccessTokenException)"),
    })
    @ApiOperation(value = "유저 및 가족 정보 얻기")
    @GetMapping()
    public ResponseEntity<CommonSuccessResponse<MemberAndFamilyMemberDTO>> getMember(

    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member member = memberService.getMemberWithFamily(memberId);
        Family family = member.getFamily();

        MemberAndFamilyMemberDTO memberAndFamilyMemberDTO = new MemberAndFamilyMemberDTO(family.getCode(), memberService.getMemberInfo(member), memberService.getFamilyMembersInfo(member));

        return ResponseEntity.ok(new CommonSuccessResponse<>("회원 및 가족 정보 불러오기 성공", memberAndFamilyMemberDTO, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원의 태그 정보 수정에 성공했습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Access Token 입니다.(ExpiredAccessTokenException)"),
    })
    @ApiOperation(value = "유저 개인 태그 업데이트")
    @PutMapping("/tags")
    public ResponseEntity<CommonSuccessResponse<MemberDTO>> updateMemberTag(
            @RequestBody UpdateMemberTagVO updateMemberTagVO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member member = memberService.getMember(memberId);

        memberService.updateMemberTag(member, updateMemberTagVO.getTags());

        MemberDTO memberInfo = memberService.getMemberInfo(member);

        return ResponseEntity.ok(new CommonSuccessResponse<>("회원 개인 태그 업데이트 성공", memberInfo, true));
    }

//    @ApiResponses({
//            @ApiResponse(code = 200, message = "회원의 가족들의 정보 가져오기를 성공했습니다.."),
//            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
//            @ApiResponse(code = 401, message = "만료된 Access Token 입니다.(ExpiredAccessTokenException)"),
//    })
//    @ApiOperation(value = "가족들 정보 얻기")
//    @GetMapping("/{id}/family")
//    public ResponseEntity<CommonSuccessResponse<MemberAndFamilyMemberDTO>> getFamilyMembers(
//            ,
//            @PathVariable("id") int memberId
//    ) {
//        tokenService.validateAccessTokenExpired(accessToken);
//
//        Member member = memberService.getMemberWithFamily(memberId);
//        Family family = member.getFamily();
//
//        MemberAndFamilyMemberDTO memberAndFamilyMemberDTO = new MemberAndFamilyMemberDTO(family.getCode(), memberService.getMemberInfo(member), memberService.getFamilyMembersInfo(member));
//
//        return ResponseEntity.ok(new CommonSuccessResponse<>("회원 및 가족 정보 불러오기 성공", memberAndFamilyMemberDTO, true));
//    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원의 가족 이름 정보 수정에 성공했습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Access Token 입니다.(ExpiredAccessTokenException)"),
    })
    @ApiOperation(value = "가족의 이름 별명으로 바꾸기")
    @PutMapping("/family/names")
    public ResponseEntity<CommonSuccessResponse<MemberDTO>> updateFamilyMemberName(
            @RequestBody UpdateMemberFamilyNameVO updateMemberFamilyNameVO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member member = memberService.getMember(memberId);

        memberService.updateMemberFamilyName(member, updateMemberFamilyNameVO.getMemberFamilyName());

        MemberDTO memberInfo = memberService.getMemberInfo(member);

        return ResponseEntity.ok(new CommonSuccessResponse<>("회원의 가족 이름 변경 성공", memberInfo, true));
    }
}