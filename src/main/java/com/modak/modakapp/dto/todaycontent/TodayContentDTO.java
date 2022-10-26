package com.modak.modakapp.dto.todaycontent;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@ApiModel(value = "오늘의 컨텐츠 응답 정보")
public class TodayContentDTO {
    private int id;

    private String title;

    private String type;

    private String description;

    private String url;
}
