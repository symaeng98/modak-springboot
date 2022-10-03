package com.modak.modakapp.vo.todaytalk;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

@Getter
@ApiModel(value = "오늘의 한 마디 요청 데이터")
public class TodayTalkVO {
    private String content;

    private String date;
}
