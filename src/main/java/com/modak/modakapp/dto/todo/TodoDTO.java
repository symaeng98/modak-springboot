package com.modak.modakapp.dto.todo;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TodoDTO {
    private final int todoId;
    private final String title;
    private final String memo;
    private final String timeTag;
    private final String repeatTag;
    private final int isDone;
    private final int memberId;
    private final String color;
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
        this.groupTodoId = groupTodoId;
    }
}
