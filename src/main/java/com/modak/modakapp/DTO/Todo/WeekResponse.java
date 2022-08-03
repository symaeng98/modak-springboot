package com.modak.modakapp.DTO.Todo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class WeekResponse {
    private Map<String,List<String>> color;
    private Map<String,List<DataDTO>> items;
    private int gauge;

    public WeekResponse(Map<String, List<String>> color, Map<String, List<DataDTO>> items) {
        this.color = color;
        this.items = items;
    }
}