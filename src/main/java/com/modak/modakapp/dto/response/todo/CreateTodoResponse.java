package com.modak.modakapp.dto.response.todo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(value = "할 일 등록에 대한 응답 정보")
public class CreateTodoResponse {
    private int newTodoId;
    private TodoResponse updateLists;
}