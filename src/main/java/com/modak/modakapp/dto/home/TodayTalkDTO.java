package com.modak.modakapp.dto.home;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class TodayTalkDTO {
    private Map<String, Map<Integer, String>> result;
}

