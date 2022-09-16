package com.modak.modakapp.dto.response.todo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateSingleTodoResponse {
    private int updatedTodoId;
    private TodoResponse updateLists;
}
