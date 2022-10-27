package com.modak.modakapp.vo.anniversary;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "기념일 등록 및 수정 요청 데이터")
public class AnniversaryVO {
    @NotNull(message = "제목을 입력해주세요.")
    private String title;

    @NotNull(message = "날짜를 입력해주세요")
    private String date;

    @NotNull(message = "카테고리를 입력해주세요.")
    private String category;

    private String memo;

    private int isYear;

    private int isLunar;

    private String fromDate;

    private String toDate;
}
