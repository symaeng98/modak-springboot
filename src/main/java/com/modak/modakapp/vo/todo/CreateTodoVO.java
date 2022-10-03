package com.modak.modakapp.vo.todo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

import java.util.List;

@Getter
@ApiModel(value = "할 일 등록 요청 데이터")
public class CreateTodoVO {
    private String title;

    private String memo;

    private String date;

    private int memberId;

    private String timeTag;

    private String fromDate;

    private String toDate;

    private List<Integer> repeat;
}
