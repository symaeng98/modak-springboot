package com.modak.modakapp.dto.home;

import com.modak.modakapp.dto.letter.ReceivedLettersDTO;
import com.modak.modakapp.dto.member.MemberAndFamilyMemberDTO;
import com.modak.modakapp.dto.response.anniversary.DateAnniversaryResponse;
import com.modak.modakapp.dto.response.todo.TodoResponse;
import com.modak.modakapp.dto.todaytalk.TodayTalkDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HomeDTO {
    private MemberAndFamilyMemberDTO memberAndFamilyMembers;

    private TodoResponse todayTodos;

    private TodayTalkDTO todayTalks;

    private ReceivedLettersDTO receivedNewLetters;

    private DateAnniversaryResponse anniversaries;
}
