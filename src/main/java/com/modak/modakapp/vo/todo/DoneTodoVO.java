package com.modak.modakapp.vo.todo;

import lombok.Getter;

@Getter
public class DoneTodoVO {
    private String date;
    private String fromDate;
    private String toDate;
    private int isDone; // 혹시 모르니까
}
