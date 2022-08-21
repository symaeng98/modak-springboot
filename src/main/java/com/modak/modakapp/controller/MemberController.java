package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Anniversary;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.enums.Category;
import com.modak.modakapp.domain.enums.Provider;
import com.modak.modakapp.domain.enums.Role;
import com.modak.modakapp.dto.MemberDTO;
import com.modak.modakapp.dto.MemberFamilyMemberDTO;
import com.modak.modakapp.dto.response.CommonFailResponse;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.dto.response.member.CreateMemberResponse;
import com.modak.modakapp.dto.response.member.MemberFamilyMemberInfoResponse;
import com.modak.modakapp.dto.response.member.MemberInfoResponse;
import com.modak.modakapp.dto.response.member.UpdateMemberResponse;
import com.modak.modakapp.dto.response.token.ReissueTokenResponse;
import com.modak.modakapp.exception.member.MemberAlreadyExistsException;
import com.modak.modakapp.service.AnniversaryService;
import com.modak.modakapp.service.FamilyService;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.utils.jwt.TokenService;
import com.modak.modakapp.vo.member.AccessTokenVO;
import com.modak.modakapp.vo.member.SignUpMemberVO;
import com.modak.modakapp.vo.member.SocialLoginVO;
import com.modak.modakapp.vo.member.TokenVO;
import com.modak.modakapp.vo.member.info.GetMemberInfoVO;
import com.modak.modakapp.vo.member.info.UpdateMemberFamilyNameVO;
import com.modak.modakapp.vo.member.info.UpdateMemberTagVO;
import com.modak.modakapp.vo.member.info.UpdateMemberVO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final FamilyService familyService;
    private final AnniversaryService anniversaryService;
    private final TokenService tokenService;
    private final HttpServletResponse servletResponse;
    private final String TOKEN_HEADER = "Bearer ";

    @ApiResponses({
            @ApiResponse(code = 201, message = "성공적으로 회원 가입을 마쳤습니다."),
            @ApiResponse(code = 409, message = "이미 가입된 회원입니다.(MemberAlreadyExistsException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @ApiOperation(value = "회원 가입")
    @PostMapping("/")
    public ResponseEntity<?> createMember(@RequestBody SignUpMemberVO signUpMemberVO) {
        if (memberService.isMemberExists(signUpMemberVO.getProviderId())) {
            throw new MemberAlreadyExistsException();
        }

        int familyId;
        // 처음 가입하는 회원일 때
        if (signUpMemberVO.getFamilyId() == -1) {
            Family family = Family.builder().name("행복한 우리 가족").build();
            familyId = familyService.join(family);
        } else { // 초대받은 회원일 때
            familyId = signUpMemberVO.getFamilyId();
        }

        Family family = familyService.find(familyId);
        Date birthday = Date.valueOf(signUpMemberVO.getBirthday());
        String colorForMember = memberService.getColorForMember(familyId);

        Member member = Member.builder()
                .family(family)
                .name(signUpMemberVO.getName())
                .is_lunar(signUpMemberVO.getIsLunar())
                .birthday(birthday)
                .role(Role.valueOf(signUpMemberVO.getRole()))
                .color(colorForMember)
                .provider(Provider.valueOf(signUpMemberVO.getProvider()))
                .providerId(signUpMemberVO.getProviderId())
                .chatLastJoined(Timestamp.valueOf(LocalDateTime.now()))
                .refreshToken("default refresh")
                .fcmToken("default fcm")
                .build();

        Anniversary anniversary = Anniversary.builder()
                .member(member)
                .family(family)
                .category(Category.CON)
                .isYear(1)
                .title(member.getName() + " 생일")
                .isBirthday(1)
                .isLunar(signUpMemberVO.getIsLunar())
                .startDate(birthday)
                .endDate(birthday)
                .build();

        int joinAnniversaryId = anniversaryService.join(anniversary);

        int memberId = memberService.join(member);
        String accessToken = tokenService.getAccessToken(memberId);
        String refreshToken = tokenService.getRefreshToken(memberId);
        memberService.updateRefreshToken(memberId, refreshToken);

        servletResponse.setHeader("ACCESS_TOKEN", TOKEN_HEADER + accessToken);
        servletResponse.setHeader("REFRESH_TOKEN", TOKEN_HEADER + refreshToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response("회원 생성 완료", new CreateMemberResponse(memberId, familyId, joinAnniversaryId)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 로그인을 완료했습니다."),
            @ApiResponse(code = 404, message = "회원 정보가 없습니다. 회원 가입 페이지로 이동하세요.(EmptyResultDataAccessException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @ApiOperation(value = "소셜 로그인 버튼 클릭시 호출")
    @GetMapping("/login/social")
    public ResponseEntity<?> socialLogin(@RequestHeader SocialLoginVO socialLoginVO) {
        String providerId = socialLoginVO.getProviderId();

        Member findMember = memberService.findMemberByProviderId(providerId);
        int memberId = findMember.getId();
        String newRefreshToken = tokenService.getRefreshToken(memberId);
        String newAccessToken = tokenService.getAccessToken(memberId);
        memberService.updateRefreshToken(memberId, newRefreshToken);
        servletResponse.setHeader("ACCESS_TOKEN", TOKEN_HEADER + newAccessToken);
        servletResponse.setHeader("REFRESH_TOKEN", TOKEN_HEADER + newRefreshToken);

        MemberDTO memberInfo = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok(CommonSuccessResponse.response("로그인 성공", new MemberInfoResponse(memberInfo)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "토큰 재발급을 성공했습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Refresh Token 입니다. 재로그인 하세요.(ExpiredJwtException)"),
    })
    @ApiOperation(value = "토큰 로그인")
    @GetMapping("/login/token")
    public ResponseEntity<?> tokenLogin(@RequestHeader TokenVO tokenVO) {
        String accessToken = tokenVO.getAccessToken().substring(7);
        String refreshToken = tokenVO.getRefreshToken().substring(7);

        // validation

        int memberId = tokenService.getMemberId(refreshToken);
        String newAccessToken = tokenService.getAccessToken(memberId);
        String newRefreshToken = tokenService.getRefreshToken(memberId);

        memberService.updateRefreshToken(memberId, newRefreshToken);

        servletResponse.setHeader("ACCESS_TOKEN", TOKEN_HEADER + newAccessToken);
        servletResponse.setHeader("REFRESH_TOKEN", TOKEN_HEADER + newRefreshToken);

        return ResponseEntity.ok(CommonSuccessResponse.response("토큰 재발급 성공", new ReissueTokenResponse("ACCESS_AND_REFRESH_TOKEN")));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 정보를 수정하였습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Refresh Token 입니다. 재로그인 하세요.(ExpiredJwtException)"),
    })
    @ApiOperation(value = "유저 개인 정보 변경")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMemberInfo(
            @RequestHeader AccessTokenVO accessTokenVO,
            @PathVariable("id") int memberId,
            @RequestBody UpdateMemberVO updateMemberVO
    ) {
        String accessToken = accessTokenVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        memberService.updateMember(memberId, updateMemberVO);

        Anniversary anniversary = anniversaryService.findBirthdayByMember(memberId);
        anniversaryService.updateBirthdayAndIsLunar(anniversary.getId(), updateMemberVO.getBirthday(), updateMemberVO.getIsLunar());

        return ResponseEntity.ok(CommonSuccessResponse.response("회원 정보 수정 성공", new UpdateMemberResponse(memberId)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 정보 가져오기를 성공했습니다.."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Refresh Token 입니다. 재로그인 하세요.(ExpiredJwtException)"),
    })
    @ApiOperation(value = "유저 개인 정보 얻기")
    @GetMapping("/{id}")
    public ResponseEntity<?> getMemberInfo(@PathVariable("id") int memberId, @RequestBody GetMemberInfoVO getMemberInfoVO) {
        String accessToken = getMemberInfoVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        MemberDTO memberDto = memberService.getMemberInfo(memberId);

        return ResponseEntity.ok(CommonSuccessResponse.response("회원 정보 가져오기 성공", new MemberInfoResponse(memberDto)));
    }

//    @ApiResponses({
//            @ApiResponse(code = 200, message = "회원의 가족 정보 수정에 성공했습니다."),
//            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
//            @ApiResponse(code = 401, message = "만료된 Refresh Token 입니다. 재로그인 하세요.(ExpiredJwtException)"),
//    })
//    @ApiOperation(value = "가족 ID 넘어가기")
//    @PutMapping("/member/family/{familyId}")
//    public ResponseEntity<?> updateMemberFamilyID(
//            @PathVariable("familyId") int familyId,
//            @RequestBody UpdateMemberFamilyVO updateMemberFamilyVO
//    ) {
//        String accessToken = updateMemberFamilyVO.getAccessToken().substring(7);
//        tokenService.isAccessTokenExpired(accessToken);
//        int memberId = tokenService.getMemberId(accessToken);
//
//        Family family = familyService.find(updateMemberFamilyVO.getFamilyId());
//        memberService.updateMemberFamily(memberId, family);
//
//        return ResponseEntity.ok(CommonSuccessResponse.response("회원의 가족 정보 수정 성공", new UpdateMemberResponse(memberId)));
//    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원의 태그 정보 수정에 성공했습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Refresh Token 입니다. 재로그인 하세요.(ExpiredJwtException)"),
    })
    @ApiOperation(value = "유저 개인 태그 업데이트")
    @PutMapping("/{id}/tag")
    public ResponseEntity<?> updateMemberTagInfo(
            @RequestHeader AccessTokenVO accessTokenVO,
            @PathVariable("id") int memberId,
            @RequestBody UpdateMemberTagVO updateMemberTagVO
    ) {
        String accessToken = accessTokenVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        memberService.updateMemberTag(memberId, updateMemberTagVO.getTags());

        return ResponseEntity.ok(CommonSuccessResponse.response("회원의 태그 정보 수정 성공", new UpdateMemberResponse(memberId)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원의 가족들의 정보 가져오기를 성공했습니다.."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Refresh Token 입니다. 재로그인 하세요.(ExpiredJwtException)"),
    })
    @ApiOperation(value = "가족들 정보 얻기")
    @GetMapping("/{id}/family")
    public ResponseEntity<?> getFamilyMembersInfo(@PathVariable("id") int memberId, @RequestHeader AccessTokenVO accessTokenVO) {
        String accessToken = accessTokenVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        List<MemberFamilyMemberDTO> mfmInfo = memberService.getMemberFamilyMembersInfo(memberId);

        return ResponseEntity.ok(CommonSuccessResponse.response("회원 정보 가져오기 성공", new MemberFamilyMemberInfoResponse(mfmInfo)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원의 가족 이름 정보 수정에 성공했습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Refresh Token 입니다. 재로그인 하세요.(ExpiredJwtException)"),
    })
    @ApiOperation(value = "가족의 이름 별명으로 바꾸기")
    @PutMapping("/{id}/family/name")
    public ResponseEntity<?> updateMemberFamilyNameInfo(
            @PathVariable("id") int memberId,
            @RequestBody UpdateMemberFamilyNameVO updateMemberFamilyNameVO
    ) {
        String accessToken = updateMemberFamilyNameVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        memberService.updateMemberFamilyName(memberId, updateMemberFamilyNameVO.getMemberFamilyName());

        return ResponseEntity.ok(CommonSuccessResponse.response("회원의 가족 정보 수정 성공", new UpdateMemberResponse(memberId)));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<?> handleMalformedJwtException(MalformedJwtException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response("JWT 포맷이 올바른지 확인하세요", "MalformedJwtException"));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleSignatureException(SignatureException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response("JWT 포맷이 올바른지 확인하세요", "SignatureException"));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response("만료된 Refresh Token 입니다.", "ExpiredJwtException"));
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<?> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonFailResponse.response("회원 정보가 없습니다. 회원가입 페이지로 이동하세요", "EmptyResultDataAccessException"));
    }

    @ExceptionHandler(MemberAlreadyExistsException.class)
    public ResponseEntity<?> handleMemberAlreadyExistsException(MemberAlreadyExistsException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(CommonFailResponse.response("이미 가입된 회원입니다.", "MemberAlreadyExistsException"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response(e.getMessage(), e.toString()));
    }
}