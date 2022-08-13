package com.modak.modakapp.controller.api;

import com.modak.modakapp.domain.enums.Category;
import com.modak.modakapp.domain.enums.Provider;
import com.modak.modakapp.domain.enums.Role;
import com.modak.modakapp.dto.MemberDataDTO;
import com.modak.modakapp.dto.response.CommonFailResponse;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.dto.response.member.CreateMemberResponse;
import com.modak.modakapp.dto.response.member.LoginMemberResponse;
import com.modak.modakapp.dto.response.member.MemberInfoResponse;
import com.modak.modakapp.dto.response.token.ReissueTokenResponse;
import com.modak.modakapp.vo.member.LoginMemberVO;
import com.modak.modakapp.vo.member.OpenVO;
import com.modak.modakapp.vo.member.SignUpMemberVO;
import com.modak.modakapp.domain.*;
import com.modak.modakapp.exception.member.MemberAlreadyExistsException;
import com.modak.modakapp.service.AnniversaryService;
import com.modak.modakapp.service.FamilyService;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.jwt.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import java.util.Calendar;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Slf4j
public class MemberApiController {

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
    @PostMapping("/new")
    public ResponseEntity<?> create(@RequestBody @ApiParam(value = "회원 기본 정보(familyId 없으면 -1)", required = true) SignUpMemberVO signUpMemberVO) {

        if (memberService.isMemberExists(signUpMemberVO.getProviderId())) {
            throw new MemberAlreadyExistsException();
        }
        Date birthday = Date.valueOf(signUpMemberVO.getBirthday());

        int familyId;
        // 처음 가입하는 회원이면
        if(signUpMemberVO.getFamilyId()==-1){
            Family family = Family.builder().name("행복한 우리 가족").build();
            familyId = familyService.join(family);
        }
        else{ // 이미 가족이 있는 회원이면
            familyId = signUpMemberVO.getFamilyId();
        }

        Family family = familyService.find(familyId);

        Member member = Member.builder().family(family).name(signUpMemberVO.getName()).is_lunar(signUpMemberVO.getIsLunar())
                .birthday(birthday).role(Role.valueOf(signUpMemberVO.getRole()))
                .provider(Provider.valueOf(signUpMemberVO.getProvider())).providerId(signUpMemberVO.getProviderId())
                .chatLastJoined(Timestamp.valueOf(LocalDateTime.now()))
                .refreshToken("default refresh").fcmToken("default fcm").build();

        // 저장
        int memberId = memberService.join(member);

        Anniversary anniversary = Anniversary.builder().member(member).family(family).category(Category.CON).isYear(1)
                .title(member.getName()+" 생일").isMemberBirthday(1).isLunar(signUpMemberVO.getIsLunar()).startDate(birthday).endDate(birthday).build();
        int joinAnniversaryId = anniversaryService.join(anniversary);


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
    @PostMapping("/social-login")
    public ResponseEntity<?> login(@RequestBody @ApiParam(value = "Provider, ProviderId", required = true) LoginMemberVO loginMemberVO) {
        String providerId = loginMemberVO.getProviderId();

        Member findMember = memberService.findMemberByProviderId(providerId);
        int memberId = findMember.getId();
        String newRefreshToken = tokenService.getRefreshToken(memberId);
        String newAccessToken = tokenService.getAccessToken(memberId);
        memberService.updateRefreshToken(memberId, newRefreshToken);
        servletResponse.setHeader("ACCESS_TOKEN", TOKEN_HEADER + newAccessToken);
        servletResponse.setHeader("REFRESH_TOKEN", TOKEN_HEADER + newRefreshToken);

        MemberDataDTO memberInfo = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok(CommonSuccessResponse.response("로그인 성공", new MemberInfoResponse(memberInfo)));

    }


    // 토큰 로그인 로직 바꾸기

    /**
     * @param openVO accessToken, refreshToken
     * @return accessToken
     * accessToken이 만료되었다는 가정 하에 refreshToken이 만료되지 않았다면 accessToken 발급
     */
    @ApiResponses({
            @ApiResponse(code = 200, message = "토큰 재발급을 성공했습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Refresh Token 입니다. 재로그인 하세요.(ExpiredJwtException)"),
    })
    @PostMapping("/token-login")
    public ResponseEntity<?> reissue(@RequestBody @ApiParam(value = "가지고 있는 Access 토큰과 Refresh 토큰", required = true) OpenVO openVO) {
        // bearer
        String accessToken = openVO.getAccessToken().substring(7);
        String refreshToken = openVO.getRefreshToken().substring(7);
        int memberId = tokenService.getMemberId(refreshToken);
        String newAccessToken = tokenService.getAccessToken(memberId);
        String newRefreshToken = tokenService.getRefreshToken(memberId);

        memberService.updateRefreshToken(memberId, newRefreshToken);

        servletResponse.setHeader("ACCESS_TOKEN", TOKEN_HEADER + newAccessToken);
        servletResponse.setHeader("REFRESH_TOKEN", TOKEN_HEADER + newRefreshToken);

        return ResponseEntity.ok(CommonSuccessResponse.response("토큰 재발급 성공", new ReissueTokenResponse("ACCESS_AND_REFRESH_TOKEN")));
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