package com.modak.modakapp.vo.todo;

import lombok.Getter;

import java.util.List;

@Getter
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
