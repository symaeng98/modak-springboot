package com.modak.modakapp.controller.api;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.dto.response.CommonFailResponse;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.dto.response.member.UpdateMemberResponse;
import com.modak.modakapp.jwt.TokenService;
import com.modak.modakapp.service.FamilyService;
import com.modak.modakapp.vo.member.UpdateMemberFamilyVO;
import com.modak.modakapp.vo.member.UpdateMemberVO;
import com.modak.modakapp.exception.member.MemberAlreadyExistsException;
import com.modak.modakapp.service.MemberService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/info")
@Slf4j
public class MemberInfoApiController {

    private final TokenService tokenService;

    private final MemberService memberService;

    private final FamilyService familyService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "토큰 재발급을 성공했습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "만료된 Refresh Token 입니다. 재로그인 하세요.(ExpiredJwtException)"),
    })
    @PutMapping("/member/{id}")
    public ResponseEntity<?> updateMemberInfo(@PathVariable("id") int memberId, @RequestBody UpdateMemberVO updateMemberVO){
        String accessToken = updateMemberVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        memberService.updateMember(memberId, updateMemberVO);

        return ResponseEntity.ok(CommonSuccessResponse.response("회원 정보 수정 성공", new UpdateMemberResponse(memberId)));
    }

    @PutMapping("/member/family/{id}")
    public ResponseEntity<?> updateMemberFamilyInfo(@PathVariable("id") int memberId, @RequestBody UpdateMemberFamilyVO updateMemberVO){
        String accessToken = updateMemberVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        Family family = familyService.find(updateMemberVO.getFamilyId());
        memberService.updateMemberFamily(memberId, family);

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
