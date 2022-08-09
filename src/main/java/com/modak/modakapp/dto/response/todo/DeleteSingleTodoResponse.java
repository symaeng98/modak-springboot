package com.modak.modakapp.dto.response.todo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteSingleTodoResponse {
    private int deletedTodoId;
    private WeekResponse updateLists;
}