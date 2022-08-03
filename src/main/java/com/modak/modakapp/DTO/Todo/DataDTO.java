package com.modak.modakapp.DTO.Todo;

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
    private int color;
}
