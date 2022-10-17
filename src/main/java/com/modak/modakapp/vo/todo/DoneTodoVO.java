package com.modak.modakapp.vo.todo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@ApiModel(value = "할 일 완료/취소 요청 데이터")
public class DoneTodoVO {
    @NotNull(message = "날짜를 입력해주세요.")
    private String date;

    private String fromDate;

    private String toDate;

    private int isDone; // 혹시 모르니까
}
