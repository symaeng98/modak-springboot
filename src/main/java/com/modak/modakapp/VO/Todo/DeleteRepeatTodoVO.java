package com.modak.modakapp.VO.Todo;

import lombok.Data;

@Data
public class DeleteRepeatTodoVO {
    private String accessToken;

    private String fromDate;

    private String toDate;

    private String date;

}