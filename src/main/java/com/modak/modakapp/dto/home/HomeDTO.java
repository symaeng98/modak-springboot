package com.modak.modakapp.dto.home;

import com.modak.modakapp.domain.TodayFortune;
import com.modak.modakapp.dto.member.MemberAndFamilyMemberDTO;
import com.modak.modakapp.dto.response.anniversary.DateAnniversaryResponse;
import com.modak.modakapp.dto.response.todo.TodoResponse;
import com.modak.modakapp.dto.todaytalk.TodayTalkDTO;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@ApiModel(value = "홈 화면 정보")
public class HomeDTO {
    private MemberAndFamilyMemberDTO memberAndFamilyMembers;

    private TodoResponse todayTodos;

    private TodayTalkDTO todayTalks;

    private DateAnniversaryResponse anniversaries;

    private TodayFortune todayFortune;
}
