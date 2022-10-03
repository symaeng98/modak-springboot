package com.modak.modakapp.vo.letter;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

@Getter
@ApiModel(value = "편지 등록 요청 데이터")
public class LetterVO {
    private int toMemberId;

    private String content;

    private String date;

    private String envelope;
}
