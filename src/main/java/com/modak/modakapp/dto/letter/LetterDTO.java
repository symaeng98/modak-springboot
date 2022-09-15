package com.modak.modakapp.dto.letter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LetterDTO {
    private int fromMemberId;

    private int toMemberId;

    private String content;

    private String date;

    private String envelope;
}
