package com.modak.modakapp.DTO.Todo;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DataDTO {
    private int todoId;
    private String title;
    private String memo;
    private String timeTag;
    private String repeatTag;
    private int isDone;
    private int memberId;
    private String color;

    @Builder

    public DataDTO(int todoId, String title, String memo, String timeTag, String repeatTag, int isDone, int memberId, String color) {
        this.todoId = todoId;
        this.title = title;
        this.memo = memo;
        this.timeTag = timeTag;
        this.repeatTag = repeatTag;
        this.isDone = isDone;
        this.memberId = memberId;
        this.color = color;
    }
}
