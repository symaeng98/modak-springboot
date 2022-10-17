package com.modak.modakapp.vo.todo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@ApiModel(value = "할 일 수정 요청 데이터")
public class UpdateTodoVO {
    @NotNull(message = "제목을 입력해주세요.")
    private String title;

    private String memo;

    @NotNull(message = "담당자를 입력해주세요.")
    private int memberId;

    @NotNull(message = "날짜를 입력해주세요.")
    private String date;

    private String timeTag;

    private String fromDate;

    private String toDate;

    private List<Integer> repeat;

    @NotNull(message = "이후 수정인지 아닌지를 입력해주세요.")
    private int isAfterUpdate;
}
