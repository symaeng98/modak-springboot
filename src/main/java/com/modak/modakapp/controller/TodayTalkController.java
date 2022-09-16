package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.TodayTalk;
import com.modak.modakapp.dto.response.CommonFailResponse;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.dto.todaytalk.TodayTalkDTO;
import com.modak.modakapp.exception.member.NoSuchMemberException;
import com.modak.modakapp.exception.todaytalk.AlreadyExistsTodayTalkException;
import com.modak.modakapp.exception.token.ExpiredAccessTokenException;
import com.modak.modakapp.exception.token.ExpiredRefreshTokenException;
import com.modak.modakapp.exception.token.NotMatchRefreshTokenException;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.service.TodayTalkService;
import com.modak.modakapp.utils.jwt.TokenService;
import com.modak.modakapp.vo.todaytalk.TodayTalkVO;
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
@RequestMapping("/api/today-talk")
@Slf4j
public class TodayTalkController {
    private final MemberService memberService;
    private final TodayTalkService todayTalkService;
    private final TokenService tokenService;
    private final String ACCESS_TOKEN = "Access-Token";

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 회원의 오늘의 한 마디를 등록했습니다."),
            @ApiResponse(code = 404, message = "회원 정보가 없습니다. (NoSuchMemberException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @ApiOperation(value = "회원의 오늘 한 마디 등록")
    @PostMapping("/{member_id}")
    public ResponseEntity<CommonSuccessResponse<TodayTalkDTO>> createTodayTalk(
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @PathVariable("member_id") int memberId,
            @RequestBody TodayTalkVO todayTalkVO
    ) {
        tokenService.validateAccessTokenExpired(accessToken);

        Member member = memberService.getMemberWithFamily(memberId);

        if (todayTalkService.isTodayTalkExists(member, Date.valueOf(todayTalkVO.getDate()))) {
            throw new AlreadyExistsTodayTalkException("이미 해당 회원의 오늘 한 마디가 존재합니다.");
        }

        TodayTalk todayTalk = TodayTalk.builder()
                .member(member)
                .family(member.getFamily())
                .content(todayTalkVO.getContent())
                .date(Date.valueOf(todayTalkVO.getDate()))
                .build();

        todayTalkService.join(todayTalk);

        Family family = member.getFamily();
        TodayTalkDTO todayTalkDto = todayTalkService.getMembersTodayTalkByDate(Date.valueOf(todayTalkVO.getDate()), Date.valueOf(todayTalkVO.getDate()), family);

        return ResponseEntity.ok(new CommonSuccessResponse<>("오늘 한 마디 등록 성공", todayTalkDto, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 회원의 오늘의 한 마디를 불러왔습니다."),
            @ApiResponse(code = 404, message = "회원 정보가 없습니다. (NoSuchMemberException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @ApiOperation(value = "회원의 오늘 한 마디 조회(날짜 범위 입력)")
    @GetMapping()
    public ResponseEntity<CommonSuccessResponse<TodayTalkDTO>> getTodayTalk(
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @RequestParam String fromDate,
            @RequestParam String toDate
    ) {
        tokenService.validateAccessTokenExpired(accessToken);

        int memberId = tokenService.getMemberId(accessToken.substring(7));
        Family family = memberService.getMemberWithFamily(memberId).getFamily();

        TodayTalkDTO todayTalkDto = todayTalkService.getMembersTodayTalkByDate(Date.valueOf(fromDate), Date.valueOf(toDate), family);

        return ResponseEntity.ok(new CommonSuccessResponse<>("오늘 한 마디 조회 성공", todayTalkDto, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 회원의 오늘의 한 마디를 수정했습니다."),
            @ApiResponse(code = 404, message = "회원 정보가 없습니다. (NoSuchMemberException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @ApiOperation(value = "회원의 오늘 한 마디 수정")
    @PutMapping("/{member_id}")
    public ResponseEntity<CommonSuccessResponse<TodayTalkDTO>> updateTodayTalk(
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @PathVariable("member_id") int memberId,
            @RequestBody TodayTalkVO todayTalkVO
    ) {
        tokenService.validateAccessTokenExpired(accessToken);

        Member member = memberService.getMemberWithFamily(memberId);

        TodayTalk todayTalk = todayTalkService.getTodayTalkByMemberAndDate(member, Date.valueOf(todayTalkVO.getDate()));

        todayTalkService.updateContent(todayTalk, todayTalkVO.getContent());
        TodayTalkDTO todayTalkDto = todayTalkService.getMembersTodayTalkByDate(Date.valueOf(todayTalkVO.getDate()), Date.valueOf(todayTalkVO.getDate()), member.getFamily());

        return ResponseEntity.ok(new CommonSuccessResponse<>("오늘 한 마디 등록 성공", todayTalkDto, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 회원의 오늘의 한 마디를 삭제했습니다."),
            @ApiResponse(code = 404, message = "회원 정보가 없습니다. (NoSuchMemberException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @ApiOperation(value = "회원의 오늘 한 마디 삭제")
    @DeleteMapping("/{member_id}")
    public ResponseEntity<CommonSuccessResponse<TodayTalkDTO>> deleteTodayTalk(
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @PathVariable("member_id") int memberId,
            @RequestParam String date
    ) {
        tokenService.validateAccessTokenExpired(accessToken);

        Member member = memberService.getMemberWithFamily(memberId);

        TodayTalk todayTalk = todayTalkService.getTodayTalkByMemberAndDate(member, Date.valueOf(date));
        System.out.println(todayTalk.getId());

        todayTalkService.deleteTodayTalk(todayTalk);
        TodayTalkDTO todayTalkDto = todayTalkService.getMembersTodayTalkByDate(Date.valueOf(date), Date.valueOf(date), member.getFamily());

        return ResponseEntity.ok(new CommonSuccessResponse<>("오늘 한 마디 삭제 성공", todayTalkDto, true));
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

    @ExceptionHandler(AlreadyExistsTodayTalkException.class)
    public ResponseEntity<?> handleAlreadyExistsTodayTalkException(AlreadyExistsTodayTalkException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response("이미 오늘의 한 마디가 존재합니다.", "AlreadyExistsTodayTalkException"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response(e.getMessage(), e.toString()));
    }
}
