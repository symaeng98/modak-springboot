package com.modak.modakapp.dto.todayfortune;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@Builder
@ApiModel(value = "하루 한 문장 응답 정보")
public class TodayFortuneDTO {
    private int memberId;

    private String content;

    private Date date;
}