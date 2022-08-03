package com.modak.modakapp.DTO.Todo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class WeekResponse {
//    private ColorDTO color;
    private Map<String,List<String>> color;
//    private ItemDTO items;
//    private Map<String,DataDTO> items;
//    private int gauge;
}
