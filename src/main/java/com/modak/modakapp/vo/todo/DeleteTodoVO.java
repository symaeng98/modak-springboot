package com.modak.modakapp.vo.todo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@ApiModel(value = "할 일 삭제 요청 데이터")
public class DeleteTodoVO {
    private String fromDate;

    private String toDate;

    @NotNull(message = "날짜를 입력해주세요.")
    private String date;

    @NotNull(message = "이후 삭제인지 아닌지를 입력해주세요.")
    private int isAfterDelete;
}
