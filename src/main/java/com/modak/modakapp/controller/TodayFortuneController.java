package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.TodayFortune;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.dto.todayfortune.TodayFortuneDTO;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.service.TodayFortuneService;
import com.modak.modakapp.utils.jwt.TokenService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/today-fortune")
@Slf4j
public class TodayFortuneController {
    private final MemberService memberService;
    private final TodayFortuneService todayFortuneService;
    private final TokenService tokenService;
    private final String ACCESS_TOKEN = "Access-Token";

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 회원의 하루 한 문장을 가져왔습니다."),
            @ApiResponse(code = 404, message = "회원 정보가 없습니다. (NoSuchMemberException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @ApiOperation(value = "회원의 하루 한 문장 가져오기")
    @GetMapping("/{member_id}")
    public ResponseEntity<CommonSuccessResponse<TodayFortuneDTO>> getTodayFortune(
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @PathVariable("member_id") int memberId
    ) {
        tokenService.validateAccessTokenExpired(accessToken);

        Member member = memberService.getMember(memberId);
        TodayFortune todayFortune = member.getTodayFortune();
        Date todayFortuneAt = member.getTodayFortuneAt();

        // 바꿔야되는 경우
        if (todayFortune == null || todayFortuneAt.before(Date.valueOf(LocalDate.now()))) {
            TodayFortune newFortune = todayFortuneService.generateTodayFortune();
            memberService.updateTodayFortuneAndTodayFortuneAt(member, newFortune);
            todayFortune = newFortune;
            todayFortuneAt = Date.valueOf(LocalDate.now());
        }

        TodayFortuneDTO todayFortuneDto = TodayFortuneDTO.builder()
                .memberId(memberId)
                .content(todayFortune.getContent())
                .date(todayFortuneAt)
                .build();

        return ResponseEntity.ok(new CommonSuccessResponse<>("하루 한 문장 불러오기 성공", todayFortuneDto, true));
    }
}
