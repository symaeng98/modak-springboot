package com.modak.modakapp.dto.letter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class LettersDTO {
    private int count;
    private List<LetterDTO> letterList;
}
