package com.modak.modakapp.vo.todo;

import lombok.Data;

@Data
public class DeleteTodoVO {
    private String accessToken;

    private String fromDate;

    private String toDate;

}
