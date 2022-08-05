package com.modak.modakapp.DTO.Todo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTodoResponse {
    private int todoId;
    private int memberId;
    private int familyId;
    private WeekResponse updateLists;
}