package com.modak.modakapp.dto.response.todo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTodoResponse {
    private int newTodoId;
    private WeekResponse updateLists;
}