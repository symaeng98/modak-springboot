package com.modak.modakapp.dto.todo;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;

@Getter
@ApiModel(value = "한 개의 할 일에 대한 정보")
public class TodoDTO {
    private final int todoId;
    private final String title;
    private final String memo;
    private final String timeTag;
    private final String repeatTag;
    private final int isDone;
    private final int memberId;
    private final String color;
    private final String memoColor;
    private final int groupTodoId;

    @Builder
    public TodoDTO(
            int todoId,
            String title,
            String memo,
            String timeTag,
            String repeatTag,
            int isDone,
            int memberId,
            String color,
            String memoColor,
            int groupTodoId
    ) {
        this.todoId = todoId;
        this.title = title;
        this.memo = memo;
        this.timeTag = timeTag;
        this.repeatTag = repeatTag;
        this.isDone = isDone;
        this.memberId = memberId;
        this.color = color;
        this.memoColor = memoColor;
        this.groupTodoId = groupTodoId;
    }
}
