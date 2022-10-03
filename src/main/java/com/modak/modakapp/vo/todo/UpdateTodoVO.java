package com.modak.modakapp.vo.todo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

import java.util.List;

@Getter
@ApiModel(value = "할 일 수정 요청 데이터")
public class UpdateTodoVO {
    private String title;

    private String memo;

    private int memberId;

    private String date;

    private String timeTag;

    private String fromDate;

    private String toDate;

    private List<Integer> repeat;

    private int isAfterUpdate;
}
