package com.modak.modakapp.dto.response.todo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTodoResponse {
    private int newTodoId;
    private int afterTodoId;
    private WeekResponse updateLists;
}