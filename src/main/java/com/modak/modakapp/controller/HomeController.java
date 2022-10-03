package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.TodayFortune;
import com.modak.modakapp.dto.home.HomeDTO;
import com.modak.modakapp.dto.member.MemberAndFamilyMemberDTO;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.dto.response.anniversary.DateAnniversaryResponse;
import com.modak.modakapp.dto.response.todo.TodoResponse;
import com.modak.modakapp.dto.todaytalk.TodayTalkDTO;
import com.modak.modakapp.service.*;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/home")
@Slf4j
public class HomeController {
    private final MemberService memberService;
    private final TodoService todoService;
    private final TodayTalkService todayTalkService;
    private final AnniversaryService anniversaryService;
    private final TodayFortuneService todayFortuneService;
    private final String ACCESS_TOKEN = "Access-Token";

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 홈 정보 불러오기를 완료했습니다."),
            @ApiResponse(code = 401, message = "1. 만료된 토큰입니다. (ExpiredJwtException)\n2. 유효하지 않은 토큰입니다. (JwtException)\n3. 헤더에 토큰이 없습니다. (NullPointerException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요."),
    })
    @ApiOperation(value = "홈 화면에서 주는 정보")
    @GetMapping()
    public ResponseEntity<CommonSuccessResponse<HomeDTO>> getHomeInformation(
            @RequestParam String date
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member member = memberService.getMemberWithFamily(memberId);
        Family family = member.getFamily();

        // 회원 정보
        MemberAndFamilyMemberDTO memberAndFamilyDto = new MemberAndFamilyMemberDTO(family.getCode(), memberService.getMemberInfo(member), memberService.getFamilyMembersInfo(member));

        // 할 일 정보
        TodoResponse colorsAndItemsAndGaugeByDateRange = todoService.findColorsAndItemsAndGaugeByDateRange(date, date, family);

        // 오늘 가족 한 마디
        TodayTalkDTO todayTalkDto = todayTalkService.getMembersTodayTalkByDate(Date.valueOf(date), Date.valueOf(date), family);

        // 기념일
        DateAnniversaryResponse dateAnniversaryData = anniversaryService.getAnniversariesByDate(date, date, family);

        // 하루 한 문장
        TodayFortune homeTodayFortune = todayFortuneService.getHomeTodayFortune(member);

        HomeDTO homeDto = HomeDTO.builder()
                .memberAndFamilyMembers(memberAndFamilyDto)
                .todayTodos(colorsAndItemsAndGaugeByDateRange)
                .todayTalks(todayTalkDto)
                .anniversaries(dateAnniversaryData)
                .todayFortune(homeTodayFortune)
                .build();

        return ResponseEntity.ok(new CommonSuccessResponse<>("홈 화면 정보 및 기본 정보 불러오기 성공", homeDto, true));
    }
}
