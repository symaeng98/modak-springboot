package com.modak.modakapp.dto.message;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@ApiModel(value = "메타 데이터")
public class MetaDataDTO {
    private String type_code;
    private List<String> key;
    private String count;
}
