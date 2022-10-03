package com.modak.modakapp.dto.response.anniversary;


import com.modak.modakapp.dto.anniversary.AnniversaryDTO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@ApiModel(value = "날짜 별 기념일 정보")
public class DateAnniversaryResponse {
    private int annCount;
    private Map<String, List<AnniversaryDTO>> data;
}
