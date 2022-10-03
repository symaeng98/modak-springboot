package com.modak.modakapp.dto.letter;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@ApiModel(value = "편지 한 개에 대한 정보")
public class LetterDTO {
    private int letterId;

    private int fromMemberId;

    private int toMemberId;

    private String content;

    private String date;

    private String envelope;

    private int isNew;
}
