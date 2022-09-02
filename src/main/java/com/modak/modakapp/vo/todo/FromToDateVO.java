package com.modak.modakapp.vo.todo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

@Getter
@ApiModel(value = "from~to 날짜 요청 데이터")
public class FromToDateVO {
    private String fromDate;

    private String toDate;
}
