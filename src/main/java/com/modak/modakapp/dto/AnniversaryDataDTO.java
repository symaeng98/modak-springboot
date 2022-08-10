package com.modak.modakapp.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AnniversaryDataDTO {
    private int annId;
    private String title;
    private String category;
    private String memo;

    @Builder

    public AnniversaryDataDTO(int annId, String title, String category, String memo) {
        this.annId = annId;
        this.title = title;
        this.category = category;
        this.memo = memo;
    }
}

