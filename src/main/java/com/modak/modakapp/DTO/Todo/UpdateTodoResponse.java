package com.modak.modakapp.DTO.Todo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTodoResponse {
    private int newTodoId;
    private int afterTodoId;
    private WeekResponse updateLists;
}