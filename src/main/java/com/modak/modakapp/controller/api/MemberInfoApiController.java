package com.modak.modakapp.controller.api;

import com.modak.modakapp.domain.Anniversary;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.dto.MemberDataDTO;
import com.modak.modakapp.dto.MemberFamilyMemberDTO;
import com.modak.modakapp.dto.response.CommonFailResponse;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.dto.response.member.MemberFamilyMemberInfoResponse;
import com.modak.modakapp.dto.response.member.MemberInfoResponse;
import com.modak.modakapp.dto.response.member.UpdateMemberResponse;
import com.modak.modakapp.jwt.TokenService;
import com.modak.modakapp.service.AnniversaryService;
import com.modak.modakapp.service.FamilyService;
import com.modak.modakapp.vo.member.info.*;
import com.modak.modakapp.exception.member.MemberAlreadyExistsException;
import com.modak.modakapp.service.MemberService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/info")
@Slf4j
public class MemberInfoApiController {

    private final TokenService tokenService;

    private final MemberService memberService;
    private final AnniversaryService anniversaryService;

    private final FamilyService familyService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 정보를 수정하였습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Refresh Token 입니다. 재로그인 하세요.(ExpiredJwtException)"),
    })
    @PutMapping("/member")
    public ResponseEntity<?> updateMemberInfo(@ApiParam(value = "회원 수정 정보", required = true) @RequestBody UpdateMemberVO updateMemberVO){
        String accessToken = updateMemberVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);
        int memberId = tokenService.getMemberId(accessToken);

        memberService.updateMember(memberId, updateMemberVO);

        Anniversary a = anniversaryService.findBirthdayByMember(memberId);
        anniversaryService.updateBirthdayAndIsLunar(a.getId(),updateMemberVO.getBirthday(), updateMemberVO.getIsLunar());


        return ResponseEntity.ok(CommonSuccessResponse.response("회원 정보 수정 성공", new UpdateMemberResponse(memberId)));
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 정보 가져오기를 성공했습니다.."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Refresh Token 입니다. 재로그인 하세요.(ExpiredJwtException)"),
    })
    @PostMapping("/member")
    public ResponseEntity<?> getMemberInfo(@ApiParam(value = "AccessToken", required = true) @RequestBody GetMemberInfoVO getMemberInfoVO){
        String accessToken = getMemberInfoVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);
        int memberId = tokenService.getMemberId(accessToken);

        MemberDataDTO memberDto = memberService.getMemberInfo(memberId);

        return ResponseEntity.ok(CommonSuccessResponse.response("회원 정보 가져오기 성공", new MemberInfoResponse(memberDto)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원의 가족들의 정보 가져오기를 성공했습니다.."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Refresh Token 입니다. 재로그인 하세요.(ExpiredJwtException)"),
    })
    @PostMapping("/member/family-member")
    public ResponseEntity<?> getFamilyMembersInfo(@ApiParam(value = "AccessToken", required = true) @RequestBody GetMemberInfoVO getMemberInfoVO){
        String accessToken = getMemberInfoVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);
        int memberId = tokenService.getMemberId(accessToken);

        List<MemberFamilyMemberDTO> mfmInfo = memberService.getMemberFamilyMembersInfo(memberId);


        return ResponseEntity.ok(CommonSuccessResponse.response("회원 정보 가져오기 성공", new MemberFamilyMemberInfoResponse(mfmInfo)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원의 가족 정보 수정에 성공했습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Refresh Token 입니다. 재로그인 하세요.(ExpiredJwtException)"),
    })
    @PutMapping("/member/family")
    public ResponseEntity<?> updateMemberFamilyInfo(@ApiParam(value = "AccessToken과 familyId", required = true)@RequestBody UpdateMemberFamilyVO updateMemberVO){
        String accessToken = updateMemberVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);
        int memberId = tokenService.getMemberId(accessToken);

        Family family = familyService.find(updateMemberVO.getFamilyId());
        memberService.updateMemberFamily(memberId, family);

        return ResponseEntity.ok(CommonSuccessResponse.response("회원의 가족 정보 수정 성공", new UpdateMemberResponse(memberId)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원의 태그 정보 수정에 성공했습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Refresh Token 입니다. 재로그인 하세요.(ExpiredJwtException)"),
    })
    @PutMapping("/member/todo-tags")
    public ResponseEntity<?> updateMemberTagInfo(@ApiParam(value = "AccessToken과 tags(list)", required = true)@RequestBody UpdateMemberTagVO updateMemberTagVO){
        String accessToken = updateMemberTagVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);
        int memberId = tokenService.getMemberId(accessToken);

        memberService.updateMemberTag(memberId,updateMemberTagVO.getTags());

        return ResponseEntity.ok(CommonSuccessResponse.response("회원의 태그 정보 수정 성공", new UpdateMemberResponse(memberId)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원의 가족 이름 정보 수정에 성공했습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Refresh Token 입니다. 재로그인 하세요.(ExpiredJwtException)"),
    })
    @PutMapping("/member/family-name")
    public ResponseEntity<?> updateMemberFamilyNameInfo(@ApiParam(value = "AccessToken과 설정한 가족들 이름", required = true) @RequestBody UpdateMemberFamilyNameVO updateMemberFamilyNameVO){
        String accessToken = updateMemberFamilyNameVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);
        int memberId = tokenService.getMemberId(accessToken);

        memberService.updateMemberFamilyName(memberId,updateMemberFamilyNameVO.getMemberFamilyName());

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
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response(e.getMessage(), e.toString()));
    }
}
