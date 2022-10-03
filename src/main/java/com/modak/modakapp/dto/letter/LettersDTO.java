package com.modak.modakapp.dto.letter;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@ApiModel(value = "편지 목록 정보")
public class LettersDTO {
    private int count;
    private List<LetterDTO> letterList;
}
