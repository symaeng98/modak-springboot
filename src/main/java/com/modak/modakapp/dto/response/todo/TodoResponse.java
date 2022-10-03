package com.modak.modakapp.dto.response.todo;

import com.modak.modakapp.dto.todo.TodoDTO;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ApiModel(value = "날짜 별 색깔, 할 일 정보")
public class TodoResponse {
    private Map<String, List<String>> color;
    private Map<String, List<TodoDTO>> items;
    private int gauge;

    @Builder
    public TodoResponse(
            Map<String, List<String>> color,
            Map<String, List<TodoDTO>> items,
            int gauge
    ) {
        this.color = color;
        this.items = items;
        this.gauge = gauge;
    }
}
