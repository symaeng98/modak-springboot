package com.modak.modakapp.dto.anniversary;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AnniversaryDTO {
    private final int annId;
    private final String title;
    private final String category;
    private final String memo;

    @Builder

    public AnniversaryDTO(int annId, String title, String category, String memo) {
        this.annId = annId;
        this.title = title;
        this.category = category;
        this.memo = memo;
    }
}

