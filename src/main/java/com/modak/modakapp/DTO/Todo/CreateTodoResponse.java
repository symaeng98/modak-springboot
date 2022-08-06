package com.modak.modakapp.DTO.Todo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTodoResponse {
    private int todoId;
    private WeekResponse updateLists;
}