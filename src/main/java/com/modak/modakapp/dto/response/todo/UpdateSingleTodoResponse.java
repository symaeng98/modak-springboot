package com.modak.modakapp.dto.response.todo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(value = "할 일 수정에 대한 응답 정보")
public class UpdateSingleTodoResponse {
    private int updatedTodoId;
    private TodoResponse updateLists;
}
