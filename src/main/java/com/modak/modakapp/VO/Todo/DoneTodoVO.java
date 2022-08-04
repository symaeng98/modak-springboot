package com.modak.modakapp.VO.Todo;

import lombok.Getter;

@Getter
public class DoneTodoVO {
    private String accessToken;
    private String date;
    private String fromDate;
    private String toDate;
    private int isDone; // 혹시 모르니까
}
