package com.modak.modakapp.dto.anniversary;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;

@Getter
@ApiModel(value = "한 개의 기념일에 대한 정보")
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

