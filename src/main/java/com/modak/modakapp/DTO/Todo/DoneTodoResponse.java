package com.modak.modakapp.DTO.Todo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DoneTodoResponse {
    private int todoId;
    private int todoDoneId;
}