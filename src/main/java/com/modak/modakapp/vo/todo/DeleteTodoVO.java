package com.modak.modakapp.vo.todo;

import lombok.Getter;

@Getter
public class DeleteTodoVO {
    private String fromDate;

    private String toDate;

    private String date;

    private int isAfterDelete;
}
