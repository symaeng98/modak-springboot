package com.modak.modakapp.dto.todaytalk;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@ApiModel(value = "날짜 별 가족 한 마디 응답 정보")
public class TodayTalkDTO {
    private Map<String, Map<Integer, String>> result;
}

