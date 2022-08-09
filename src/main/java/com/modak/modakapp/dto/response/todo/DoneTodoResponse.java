package com.modak.modakapp.dto.response.todo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DoneTodoResponse {
    private int todoId;
    private int todoDoneId;
}