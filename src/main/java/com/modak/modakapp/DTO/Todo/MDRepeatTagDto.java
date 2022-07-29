package com.modak.modakapp.DTO.Todo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@AllArgsConstructor
public class MDRepeatTagDto {
    private List<String> repeat;
}
