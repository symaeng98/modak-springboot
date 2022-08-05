package com.modak.modakapp.VO.Todo;

import lombok.Data;

import java.util.List;

@Data
public class DeleteTodoVO {
    private String accessToken;

    private String fromDate;

    private String toDate;

}
