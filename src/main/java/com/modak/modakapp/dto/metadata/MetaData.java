package com.modak.modakapp.dto.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaData {
    private String type_code;
    private List<String> key;
    private String count;
}
