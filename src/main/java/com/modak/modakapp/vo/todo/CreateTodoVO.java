package com.modak.modakapp.vo.todo;

import lombok.Data;

import java.util.List;

@Data
public class CreateTodoVO {
    private String accessToken;

    private String title;

    private String memo;

    private String date;

    private int memberId;

    private String timeTag;

    private String fromDate;

    private String toDate;

    private List<Integer> repeat;

}
