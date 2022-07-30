package com.modak.modakapp.domain.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MDRepeatTag {
    private List<String> repeat;
}
