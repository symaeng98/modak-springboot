package com.modak.modakapp.dto.response.anniversary;


import com.modak.modakapp.dto.anniversary.AnniversaryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class DateAnniversaryResponse {
    private int annCount;
    private Map<String, List<AnniversaryDTO>> data;
}
