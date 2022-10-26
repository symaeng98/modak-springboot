package com.modak.modakapp.service;

import com.modak.modakapp.domain.TodayContent;
import com.modak.modakapp.dto.todaycontent.TodayContentDTO;
import com.modak.modakapp.repository.TodayContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TodayContentService {
    private final TodayContentRepository todayContentRepository;

    public List<TodayContentDTO> getTodayContent(String date) {
        int numOfContents = 6;
        String standardDate = "2022-10-26";

        Calendar getRequestedDate = Calendar.getInstance();
        getRequestedDate.setTime(Date.valueOf(date)); // 요청한 날짜

        Date std = Date.valueOf(standardDate);
        Calendar cmpDate = Calendar.getInstance();
        cmpDate.setTime(std); //특정 일자

        long diffSec = (getRequestedDate.getTimeInMillis() - cmpDate.getTimeInMillis()) / 1000;
        int diffDays = (int) (diffSec / (24 * 60 * 60)); //일자수 차이

        System.out.println(diffDays + "일 차이");

        // 0일 차이 나면 1,2
        // 1일 차이 나면 3,4
        // 2일 차이 나면 5,6
        // -> x%6 * 2 +1, x%6 * 2 + 2 -> 총 6 개씩
        // id>=(x%6)*2+1 and id <= (x%6) * 2 + 2
        int x1 = (diffDays % numOfContents) * 2 + 1;
        int x2 = (diffDays % numOfContents) * 2 + 2;

        List<TodayContent> todayContents = todayContentRepository.findAllById(x1, x2);

        List<TodayContentDTO> tcList = new ArrayList<>();
        todayContents.forEach(tc -> {
            TodayContentDTO tcd = TodayContentDTO.builder()
                    .id(tc.getId())
                    .title(tc.getTitle())
                    .description(tc.getDescription())
                    .url(tc.getUrl())
                    .type(tc.getType())
                    .build();
            tcList.add(tcd);
        });

        return tcList;
    }
}
