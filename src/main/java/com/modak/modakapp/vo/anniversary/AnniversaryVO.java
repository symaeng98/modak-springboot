package com.modak.modakapp.vo.anniversary;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "기념일 등록 및 수정 요청 데이터")
public class AnniversaryVO {
    private String title;
    private String date;
    private String category;
    private String memo;
    private int isYear;
    private int isLunar;
    private String fromDate;
    private String toDate;
}
