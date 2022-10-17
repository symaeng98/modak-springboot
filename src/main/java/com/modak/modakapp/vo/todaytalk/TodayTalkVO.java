package com.modak.modakapp.vo.todaytalk;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@ApiModel(value = "오늘의 한 마디 요청 데이터")
public class TodayTalkVO {
    @NotNull(message = "내용을 입력해주세요.")
    private String content;

    @NotNull(message = "날짜를 입력해주세요.")
    private String date;
}
