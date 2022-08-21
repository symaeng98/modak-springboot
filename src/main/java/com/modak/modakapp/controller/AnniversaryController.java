package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Anniversary;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.enums.Category;
import com.modak.modakapp.dto.response.CommonFailResponse;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.dto.response.anniversary.CreateAnniversaryResponse;
import com.modak.modakapp.dto.response.anniversary.DateAnniversaryResponse;
import com.modak.modakapp.exception.member.MemberAlreadyExistsException;
import com.modak.modakapp.service.AnniversaryService;
import com.modak.modakapp.service.FamilyService;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.utils.jwt.TokenService;
import com.modak.modakapp.vo.anniversary.CreateAnniversaryVO;
import com.modak.modakapp.vo.todo.WeekVO;
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

import java.sql.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/anniversary")
@Slf4j
public class AnniversaryController {
    private final TokenService tokenService;
    private final AnniversaryService anniversaryService;
    private final MemberService memberService;
    private final FamilyService familyService;

    private final String TOKENEXPIREDMESSAGE = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)";

    @ApiResponses({
            @ApiResponse(code = 201, message = "기념일 등록에 성공하였습니다."),
            @ApiResponse(code = 401, message = TOKENEXPIREDMESSAGE),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @PostMapping("/new")
    public ResponseEntity<?> create(@ApiParam(value = "기념일 생성 정보 및 fromDate, toDate", required = true) @RequestBody CreateAnniversaryVO createAnniversaryVO) {
        String accessToken = createAnniversaryVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        int memberId = tokenService.getMemberId(accessToken);
        Member findMember = memberService.findMember(memberId);

        // 가족 가져오기
        Family family = findMember.getFamily();
        int familyId = family.getId();


        // 날짜 변형
        Date date = Date.valueOf(createAnniversaryVO.getDate());

        Anniversary anniversary = Anniversary.builder().member(findMember).family(family).title(createAnniversaryVO.getTitle())
                .memo(createAnniversaryVO.getMemo()).category(Category.valueOf(createAnniversaryVO.getCategory())).isYear(createAnniversaryVO.getIsYear())
                .isLunar(createAnniversaryVO.getIsLunar()).startDate(date).endDate(date).build();

        int anniversaryId = anniversaryService.join(anniversary);

        DateAnniversaryResponse dar = anniversaryService.findDateAnniversaryData(createAnniversaryVO.getFromDate(), createAnniversaryVO.getToDate(), family);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response("기념일 생성 완료", new CreateAnniversaryResponse(familyId, anniversaryId, dar)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "기념일 업데이트에 성공하였습니다."),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") int annId, @RequestBody CreateAnniversaryVO createAnniversaryVO) {
        String accessToken = createAnniversaryVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        int memberId = tokenService.getMemberId(accessToken);
        Member findMember = memberService.findMember(memberId);

        Family family = findMember.getFamily();
        int familyId = family.getId();

        String title = createAnniversaryVO.getTitle();
        String date = createAnniversaryVO.getDate();
        String memo = createAnniversaryVO.getMemo();
        String category = createAnniversaryVO.getCategory();
        int isYear = createAnniversaryVO.getIsYear();

        String fromDate = createAnniversaryVO.getFromDate();
        String toDate = createAnniversaryVO.getToDate();

        anniversaryService.updateAnniversary(annId, title, date, date, category, memo, isYear);

        DateAnniversaryResponse dar = anniversaryService.findDateAnniversaryData(fromDate, toDate, family);

        return ResponseEntity.ok(CommonSuccessResponse.response("기념일 수정 완료", new CreateAnniversaryResponse(familyId, annId, dar)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "기념일 삭제에 성공하였습니다."),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@ApiParam(value = "기념일 삭제 id 및 fromDate, toDate", required = true) @PathVariable("id") int annId, @RequestBody WeekVO weekVO) {
        String accessToken = weekVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        anniversaryService.deleteAnniversary(annId);

        Family family = familyService.find(weekVO.getFamilyId());

        String fromDate = weekVO.getFromDate();
        String toDate = weekVO.getToDate();

        DateAnniversaryResponse dar = anniversaryService.findDateAnniversaryData(fromDate, toDate, family);

        return ResponseEntity.ok(CommonSuccessResponse.response("기념일 삭제 완료", new CreateAnniversaryResponse(family.getId(), annId, dar)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "기념일 불러오기에 성공하였습니다."),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @PostMapping("/from-to-date")
    public ResponseEntity<?> getAnniversaries(@ApiParam(value = "fromDate~toDate 기념일 정보", required = true) @RequestBody WeekVO weekVO) {
        String accessToken = weekVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        int memberId = tokenService.getMemberId(accessToken);
        Member findMember = memberService.findMember(memberId);

        // 가족 가져오기
        Family family = findMember.getFamily();


        DateAnniversaryResponse dar = anniversaryService.findDateAnniversaryData(weekVO.getFromDate(), weekVO.getToDate(), family);

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
