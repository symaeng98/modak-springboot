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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/today-fortune")
@Slf4j
public class TodayFortuneController {
    private final MemberService memberService;
    private final TodayFortuneService todayFortuneService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 회원의 하루 한 문장을 가져왔습니다."),
            @ApiResponse(code = 401, message = "1. 만료된 토큰입니다. (ExpiredJwtException)\n2. 유효하지 않은 토큰입니다. (JwtException)\n3. 헤더에 토큰이 없습니다. (NullPointerException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요."),
    })
    @ApiOperation(value = "회원의 하루 한 문장 가져오기")
    @GetMapping()
    public ResponseEntity<CommonSuccessResponse<TodayFortuneDTO>> getTodayFortune(

    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member member = memberService.getMemberWithTodayFortune(memberId);

        Date todayFortuneAt = member.getTodayFortuneAt();

        TodayFortune todayFortune;

        // 바꿔야 할 때
        if (todayFortuneAt == null || todayFortuneAt.before(Date.valueOf(LocalDate.now()))) {
            TodayFortune newFortune = todayFortuneService.generateTodayFortune();
            todayFortuneAt = Date.valueOf(LocalDate.now());
            todayFortune = newFortune;
            memberService.updateTodayFortuneAndTodayFortuneAt(member, newFortune, todayFortuneAt);
        } else {
            todayFortune = member.getTodayFortune();
        }

        TodayFortuneDTO todayFortuneDto = TodayFortuneDTO.builder()
                .memberId(memberId)
                .id(todayFortune.getId())
                .content(todayFortune.getContent())
                .date(todayFortuneAt)
                .type(todayFortune.getType())
                .build();

        return ResponseEntity.ok(new CommonSuccessResponse<>("하루 한 문장 불러오기 성공", todayFortuneDto, true));
    }
}
