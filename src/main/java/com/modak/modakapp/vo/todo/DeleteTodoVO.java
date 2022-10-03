package com.modak.modakapp.vo.todo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

@Getter
@ApiModel(value = "할 일 삭제 요청 데이터")
public class DeleteTodoVO {
    private String fromDate;

    private String toDate;

    private String date;

    private int isAfterDelete;
}
