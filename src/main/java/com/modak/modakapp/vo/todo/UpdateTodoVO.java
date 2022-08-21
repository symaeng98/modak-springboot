package com.modak.modakapp.vo.todo;

import lombok.Data;

@Data
public class UpdateTodoVO {

    private String title;

    private String memo;

    private int memberId;

    private String date;

    private String timeTag;

    private String fromDate;

    private String toDate;

}
