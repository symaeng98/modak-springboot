package com.modak.modakapp.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AnniversaryDataDTO {
    private String title;
    private String category;
    private String memo;

    @Builder

    public AnniversaryDataDTO(String title, String category, String memo) {
        this.title = title;
        this.category = category;
        this.memo = memo;
    }
}

