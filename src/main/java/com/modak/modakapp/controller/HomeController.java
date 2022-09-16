package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.dto.home.HomeDTO;
import com.modak.modakapp.dto.letter.ReceivedLettersDTO;
import com.modak.modakapp.dto.member.MemberAndFamilyMemberDTO;
import com.modak.modakapp.dto.response.CommonFailResponse;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.dto.response.anniversary.DateAnniversaryResponse;
import com.modak.modakapp.dto.response.todo.TodoResponse;
import com.modak.modakapp.dto.todaytalk.TodayTalkDTO;
import com.modak.modakapp.exception.member.NoSuchMemberException;
import com.modak.modakapp.exception.token.ExpiredAccessTokenException;
import com.modak.modakapp.exception.token.ExpiredRefreshTokenException;
import com.modak.modakapp.exception.token.NotMatchRefreshTokenException;
import com.modak.modakapp.service.*;
import com.modak.modakapp.utils.jwt.TokenService;
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

import javax.servlet.http.HttpServletResponse;
import java.sql.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
@Slf4j
public class HomeController {
    private final MemberService memberService;
    private final FamilyService familyService;
    private final TodoService todoService;
    private final TodayTalkService todayTalkService;
    private final LetterService letterService;
    private final AnniversaryService anniversaryService;
    private final TokenService tokenService;
    private final HttpServletResponse servletResponse;
    private final String TOKEN_HEADER = "Bearer ";
    private final String ACCESS_TOKEN = "Access-Token";
    private final String REFRESH_TOKEN = "Refresh-Token";

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 홈 정보 불러오기를 완료했습니다."),
            @ApiResponse(code = 404, message = "회원 정보가 없습니다. 회원 가입 페이지로 이동하세요.(NoSuchMemberException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @ApiOperation(value = "홈 화면에서 주는 정보")
    @GetMapping("/{member_id}")
    public ResponseEntity<CommonSuccessResponse<HomeDTO>> getHomeInformation(
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @RequestParam String date,
            @PathVariable("member_id") int memberId
    ) {
        Member member = memberService.getMemberWithFamily(memberId);
        Family family = member.getFamily();

        // 회원 정보
        MemberAndFamilyMemberDTO memberAndFamilyDto = new MemberAndFamilyMemberDTO(memberService.getMemberInfo(member), memberService.getFamilyMembersInfo(member));

        // 할 일 정보
        TodoResponse colorsAndItemsAndGaugeByDateRange = todoService.findColorsAndItemsAndGaugeByDateRange(date, date, family);

        // 오늘 가족 한 마디
        TodayTalkDTO todayTalkDto = todayTalkService.getMembersTodayTalkByDate(Date.valueOf(date), Date.valueOf(date), family);

        // 새롭게 받은 편지 목록
        ReceivedLettersDTO receivedNewLettersDto = letterService.getReceivedNewLettersByMember(member);

        // 기념일
        DateAnniversaryResponse dateAnniversaryData = anniversaryService.getAnniversariesByDate(date, date, family);

        HomeDTO homeDto = HomeDTO.builder()
                .memberAndFamilyMembers(memberAndFamilyDto)
                .todayTodos(colorsAndItemsAndGaugeByDateRange)
                .todayTalks(todayTalkDto)
                .receivedNewLetters(receivedNewLettersDto)
                .anniversaries(dateAnniversaryData)
                .build();

        return ResponseEntity.ok(new CommonSuccessResponse<>("홈 화면 정보 및 기본 정보 불러오기 성공", homeDto, true));
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
