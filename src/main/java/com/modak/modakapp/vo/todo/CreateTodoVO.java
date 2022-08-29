package com.modak.modakapp.vo.todo;

import lombok.Getter;

import java.util.List;

@Getter
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
