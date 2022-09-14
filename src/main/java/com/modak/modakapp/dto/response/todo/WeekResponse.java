package com.modak.modakapp.dto.response.todo;

import com.modak.modakapp.dto.todo.TodoDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class WeekResponse {
    private Map<String, List<String>> color;
    private Map<String, List<TodoDTO>> items;
    private int gauge;

    @Builder
    public WeekResponse(
            Map<String, List<String>> color,
            Map<String, List<TodoDTO>> items,
            int gauge
    ) {
        this.color = color;
        this.items = items;
        this.gauge = gauge;
    }
}
