package com.modak.modakapp.DTO.Todo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateSingleTodoResponse {
    private int updatedTodoId;
    private WeekResponse updateLists;
}
