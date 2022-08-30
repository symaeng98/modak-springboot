package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Anniversary;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.enums.Category;
import com.modak.modakapp.dto.response.CommonFailResponse;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.dto.response.anniversary.AnniversaryResponse;
import com.modak.modakapp.dto.response.anniversary.DateAnniversaryResponse;
import com.modak.modakapp.exception.token.ExpiredAccessTokenException;
import com.modak.modakapp.exception.token.ExpiredRefreshTokenException;
import com.modak.modakapp.service.AnniversaryService;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.utils.jwt.TokenService;
import com.modak.modakapp.vo.anniversary.AnniversaryVO;
import com.modak.modakapp.vo.todo.FromToDateVO;
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

import java.sql.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/anniversary")
@Slf4j
public class AnniversaryController {
    private final TokenService tokenService;
    private final AnniversaryService anniversaryService;
    private final MemberService memberService;

    // 공통되는 응답 코멘트 부분 변수로 묶기 - 추후 (ACCESS_TOKEN_EXCEPTION_MESSAGE)
    @ApiResponses({
            @ApiResponse(code = 201, message = "기념일 생성을 성공하였습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "1. Access Token이 만료되었습니다.(ExpiredAccessTokenException)\n2. 만료된 Refresh Token 입니다. 다시 로그인하세요.(ExpiredRefreshTokenException)"),
    })
    @ApiOperation(value = "기념일 생성")
    @PostMapping()
    public ResponseEntity<?> createAnniversary(
            @ApiParam(value = "기념일 생성 정보 및 fromDate, toDate", required = true)
            @RequestHeader(value = "ACCESS_TOKEN") String accessToken,
            @RequestBody AnniversaryVO anniversaryVO
    ) {
        // Access Token 검증
        String subAccessToken = tokenService.validateAccessTokenExpired(accessToken);

        // 회원 id 가져와서 회원 찾기
        int memberId = tokenService.getMemberId(subAccessToken);
        Member memberWithFamily = memberService.findMemberWithFamily(memberId);

        Family family = memberWithFamily.getFamily();
        int familyId = family.getId();


        // 날짜 변형
        Date date = Date.valueOf(anniversaryVO.getDate());

        Anniversary anniversary = Anniversary.builder()
                .member(memberWithFamily)
                .family(family)
                .title(anniversaryVO.getTitle())
                .memo(anniversaryVO.getMemo())
                .category(Category.valueOf(anniversaryVO.getCategory()))
                .isYear(anniversaryVO.getIsYear())
                .isLunar(anniversaryVO.getIsLunar())
                .startDate(date)
                .endDate(date)
                .build();

        int anniversaryId = anniversaryService.join(anniversary);

        DateAnniversaryResponse dar = anniversaryService.findDateAnniversaryData(anniversaryVO.getFromDate(), anniversaryVO.getToDate(), family);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response("기념일 생성 완료", new AnniversaryResponse(familyId, anniversaryId, dar)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "기념일 정보 변경에 성공하였습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "1. Access Token이 만료되었습니다.(ExpiredAccessTokenException)\n2. 만료된 Refresh Token 입니다. 다시 로그인하세요.(ExpiredRefreshTokenException)"),
    })
    @ApiOperation(value = "기념일 정보 변경")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAnniversary(
            @RequestHeader(value = "ACCESS_TOKEN") String accessToken,
            @PathVariable("id") int annId,
            @RequestBody AnniversaryVO anniversaryVO
    ) {
        // Access Token 검증
        String subAccessToken = tokenService.validateAccessTokenExpired(accessToken);

        // 회원 id 가져와서 회원 찾기
        int memberId = tokenService.getMemberId(subAccessToken);
        Member memberWithFamily = memberService.findMemberWithFamily(memberId);

        Family family = memberWithFamily.getFamily();
        int familyId = family.getId();

        anniversaryService.updateAnniversary(annId, anniversaryVO);

        DateAnniversaryResponse dar = anniversaryService.findDateAnniversaryData(anniversaryVO.getFromDate(), anniversaryVO.getToDate(), family);

        return ResponseEntity.ok(CommonSuccessResponse.response("기념일 수정 완료", new AnniversaryResponse(familyId, annId, dar)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "기념일 삭제에 성공하였습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "1. Access Token이 만료되었습니다.(ExpiredAccessTokenException)\n2. 만료된 Refresh Token 입니다. 다시 로그인하세요.(ExpiredRefreshTokenException)"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnniversary(
            @ApiParam(value = "기념일 삭제 id 및 fromDate, toDate", required = true)
            @RequestHeader(value = "ACCESS_TOKEN") String accessToken,
            @PathVariable("id") int annId,
            @RequestBody FromToDateVO fromToDateVO
    ) {
        // Access Token 검증
        String subAccessToken = tokenService.validateAccessTokenExpired(accessToken);

        // 회원 id 가져와서 회원 찾기
        int memberId = tokenService.getMemberId(subAccessToken);
        Member memberWithFamily = memberService.findMemberWithFamily(memberId);

        Family family = memberWithFamily.getFamily();
        int familyId = family.getId();

        anniversaryService.deleteAnniversary(annId);

        DateAnniversaryResponse dar = anniversaryService.findDateAnniversaryData(fromToDateVO.getFromDate(), fromToDateVO.getToDate(), family);

        return ResponseEntity.ok(CommonSuccessResponse.response("기념일 삭제 완료", new AnniversaryResponse(familyId, annId, dar)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "기념일 불러오기에 성공하였습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "1. Access Token이 만료되었습니다.(ExpiredAccessTokenException)\n2. 만료된 Refresh Token 입니다. 다시 로그인하세요.(ExpiredRefreshTokenException)"),
    })
    @PostMapping("/from-to-date")
    public ResponseEntity<?> getAnniversaries(
            @ApiParam(value = "fromDate~toDate 기념일 정보", required = true)
            @RequestHeader(value = "ACCESS_TOKEN") String accessToken,
            @RequestBody FromToDateVO fromToDateVO
    ) {
        // Access Token 검증
        String subAccessToken = tokenService.validateAccessTokenExpired(accessToken);

        // 회원 id 가져와서 회원 찾기
        int memberId = tokenService.getMemberId(subAccessToken);
        Member memberWithFamily = memberService.findMemberWithFamily(memberId);

        Family family = memberWithFamily.getFamily();

        DateAnniversaryResponse dar = anniversaryService.findDateAnniversaryData(fromToDateVO.getFromDate(), fromToDateVO.getToDate(), family);

        return ResponseEntity.ok(CommonSuccessResponse.response("해당 날짜의 기념일을 불러왔습니다.", dar));
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

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<?> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonFailResponse.response("회원 정보가 없습니다. 회원가입 페이지로 이동하세요", "EmptyResultDataAccessException"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response(e.getMessage(), e.toString()));
    }
}
