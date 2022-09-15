package com.modak.modakapp.dto.home;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@Builder
public class TodayFortuneDTO {
    private int memberId;

    private String content;

    private Date date;
}