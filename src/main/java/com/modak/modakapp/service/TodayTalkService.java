package com.modak.modakapp.service;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.TodayTalk;
import com.modak.modakapp.dto.todaytalk.TodayTalkDTO;
import com.modak.modakapp.exception.todaytalk.NoSuchTodayTalkException;
import com.modak.modakapp.repository.TodayTalkRepository;
import com.modak.modakapp.utils.todo.TodoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TodayTalkService {
    private final TodayTalkRepository todayTalkRepository;
    private final TodoUtil todoUtil;

    @Transactional
    public int join(TodayTalk todayTalk) {
        todayTalkRepository.save(todayTalk);
        return todayTalk.getId();
    }

    public TodayTalk getTodayTalkByMemberAndDate(Member member, Date date) {
        return todayTalkRepository.findTodayTalkByMemberAndDate(member, date).orElseThrow(() -> new NoSuchTodayTalkException("조건에 해당하는 가족 한 마디가 존재하지 않습니다."));
    }

    @Transactional
    public void updateContent(TodayTalk todayTalk, String content) {
        todayTalk.changeContent(content);
    }

    public TodayTalkDTO getMembersTodayTalkByDate(Date startDate, Date endDate, Family family) {
        List<TodayTalk> todayTalkList = todayTalkRepository.findTodayTalkByDate(startDate, endDate, family.getId());

        // 시작일 ~ 종료일 String List 로 변환
        List<String> dates = todoUtil.getFromToDateList(startDate.toString(), endDate.toString());

        Map<String, Map<Integer, String>> result = new HashMap<>();

        dates.forEach(d -> {
            Map<Integer, String> list = new HashMap<>();
            todayTalkList.forEach(tt -> {
                if (tt.getDate().equals(Date.valueOf(d))) {
                    list.put(tt.getMember().getId(), tt.getContent());
                }
            });
            result.put(d, list);
        });

        return TodayTalkDTO.builder()
                .result(result)
                .build();
    }

    @Transactional
    public void deleteTodayTalk(TodayTalk todayTalk) {
        todayTalk.removeTodayTalk(Timestamp.valueOf(LocalDateTime.now()));
    }

    public boolean isTodayTalkExists(Member member, Date date) {
        return todayTalkRepository.isExists(member, date);
    }
}