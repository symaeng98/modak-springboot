package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.TodayTalk;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.dto.todaytalk.TodayTalkDTO;
import com.modak.modakapp.exception.todaytalk.AlreadyExistsTodayTalkException;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.service.TodayTalkService;
import com.modak.modakapp.utils.jwt.TokenService;
import com.modak.modakapp.vo.todaytalk.TodayTalkVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/today-talk")
@Slf4j
public class TodayTalkController {
    private final MemberService memberService;
    private final TodayTalkService todayTalkService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 회원의 오늘의 한 마디를 등록했습니다."),
            @ApiResponse(code = 404, message = "회원 정보가 없습니다. (NoSuchMemberException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @ApiOperation(value = "회원의 오늘 한 마디 등록")
    @PostMapping()
    public ResponseEntity<CommonSuccessResponse<TodayTalkDTO>> createTodayTalk(
            @RequestBody @Valid TodayTalkVO todayTalkVO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

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
            @RequestParam String fromDate,
            @RequestParam String toDate
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

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
    @PutMapping()
    public ResponseEntity<CommonSuccessResponse<TodayTalkDTO>> updateTodayTalk(
            @RequestBody @Valid TodayTalkVO todayTalkVO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member member = memberService.getMemberWithFamily(memberId);

        TodayTalk todayTalk = todayTalkService.getTodayTalkByMemberAndDate(member, Date.valueOf(todayTalkVO.getDate()));

        todayTalkService.updateContent(todayTalk, todayTalkVO.getContent());
        TodayTalkDTO todayTalkDto = todayTalkService.getMembersTodayTalkByDate(Date.valueOf(todayTalkVO.getDate()), Date.valueOf(todayTalkVO.getDate()), member.getFamily());

        return ResponseEntity.ok(new CommonSuccessResponse<>("오늘 한 마디 등록 성공", todayTalkDto, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 회원의 오늘의 한 마디를 삭제했습니다."),
            @ApiResponse(code = 401, message = "1. 만료된 토큰입니다. (ExpiredJwtException)\n2. 유효하지 않은 토큰입니다. (JwtException)\n3. 헤더에 토큰이 없습니다. (NullPointerException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요."),
    })
    @ApiOperation(value = "회원의 오늘 한 마디 삭제")
    @DeleteMapping()
    public ResponseEntity<CommonSuccessResponse<TodayTalkDTO>> deleteTodayTalk(
            @RequestParam String date
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member member = memberService.getMemberWithFamily(memberId);

        TodayTalk todayTalk = todayTalkService.getTodayTalkByMemberAndDate(member, Date.valueOf(date));
        System.out.println(todayTalk.getId());

        todayTalkService.deleteTodayTalk(todayTalk);
        TodayTalkDTO todayTalkDto = todayTalkService.getMembersTodayTalkByDate(Date.valueOf(date), Date.valueOf(date), member.getFamily());

        return ResponseEntity.ok(new CommonSuccessResponse<>("오늘 한 마디 삭제 성공", todayTalkDto, true));
    }
}
