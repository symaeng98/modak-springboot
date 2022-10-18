package com.modak.modakapp.dto.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaData {
    private String type_code;
    private String key;
    private String count;
}
