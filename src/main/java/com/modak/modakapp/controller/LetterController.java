package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Letter;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.dto.letter.SentLettersDTO;
import com.modak.modakapp.dto.response.CommonFailResponse;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.exception.member.NoSuchMemberException;
import com.modak.modakapp.exception.token.ExpiredAccessTokenException;
import com.modak.modakapp.exception.token.ExpiredRefreshTokenException;
import com.modak.modakapp.exception.token.NotMatchRefreshTokenException;
import com.modak.modakapp.service.LetterService;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.utils.jwt.TokenService;
import com.modak.modakapp.vo.letter.LetterVO;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/letter")
@Slf4j
public class LetterController {
    private final MemberService memberService;
    private final TokenService tokenService;
    private final LetterService letterService;
    private final String ACCESS_TOKEN = "Access-Token";

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 편지를 등록했습니다."),
            @ApiResponse(code = 404, message = "회원 정보가 없습니다. (NoSuchMemberException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @ApiOperation(value = "편지 등록")
    @PostMapping("/{member_id}")
    public ResponseEntity<CommonSuccessResponse<SentLettersDTO>> createLetter(
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @PathVariable("member_id") int memberId,
            @RequestBody LetterVO letterVO
    ) {
        tokenService.validateAccessTokenExpired(accessToken);

        Member fromMember = memberService.getMemberWithFamily(memberId);
        Member toMember = memberService.getMember(letterVO.getToMemberId());

        Letter letter = Letter.builder()
                .fromMember(fromMember)
                .toMember(toMember)
                .content(letterVO.getContent())
                .date(Date.valueOf(letterVO.getDate()))
                .envelope("default")
                .family(fromMember.getFamily())
                .build();

        letterService.join(letter);

        SentLettersDTO sentLettersDto = letterService.getLettersByMember(fromMember);

        return ResponseEntity.ok(new CommonSuccessResponse<>("편지 등록 성공", sentLettersDto, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 편지 목록을 가져왔습니다."),
            @ApiResponse(code = 404, message = "회원 정보가 없습니다. (NoSuchMemberException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @ApiOperation(value = "편지 목록 조회")
    @GetMapping("/{member_id}")
    public ResponseEntity<CommonSuccessResponse<SentLettersDTO>> getSentLetter(
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @PathVariable("member_id") int memberId
    ) {
        tokenService.validateAccessTokenExpired(accessToken);

        Member member = memberService.getMemberWithFamily(memberId);

        SentLettersDTO sentLettersDto = letterService.getLettersByMember(member);

        return ResponseEntity.ok(new CommonSuccessResponse<>("회원의 편지 목록 불러오기 성공", sentLettersDto, true));
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


    @ExceptionHandler(ExpiredAccessTokenException.class)
    public ResponseEntity<?> handleExpiredAccessTokenException(ExpiredAccessTokenException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response("만료된 Access Token 입니다.", "ExpiredAccessTokenException"));
    }

    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ResponseEntity<?> handleExpiredRefreshTokenException(ExpiredRefreshTokenException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response("만료된 Refresh Token 입니다. 다시 로그인하세요", "ExpiredRefreshTokenException"));
    }

    @ExceptionHandler(NotMatchRefreshTokenException.class)
    public ResponseEntity<?> handleNotMatchRefreshTokenException(NotMatchRefreshTokenException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response("회원이 가지고 있는 Refresh Token과 요청한 Refresh Token이 다릅니다.", "NotMatchRefreshTokenException"));
    }

    @ExceptionHandler(NoSuchMemberException.class)
    public ResponseEntity<?> handleNoSuchMemberException(NoSuchMemberException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonFailResponse.response("회원 정보가 없습니다. 회원가입 페이지로 이동하세요", "NoSuchMemberException"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response(e.getMessage(), e.toString()));
    }
}
