package com.modak.modakapp.vo.letter;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@ApiModel(value = "편지 등록 요청 데이터")
public class LetterVO {
    @NotNull(message = "보낼 사용자의 id를 입력해주세요")
    private int toMemberId;

    @NotNull(message = "내용을 입력해주세요")
    private String content;

    @NotNull(message = "날짜를 입력해주세요.")
    private String date;

    @NotNull(message = "봉투를 입력해주세요.")
    private String envelope;
}
