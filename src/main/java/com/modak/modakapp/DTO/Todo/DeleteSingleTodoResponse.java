package com.modak.modakapp.DTO.Todo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteSingleTodoResponse {
    private int deletedTodoId;
    private WeekResponse updateLists;
}