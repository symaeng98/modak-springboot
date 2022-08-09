package com.modak.modakapp.dto.response.anniversary;


import com.modak.modakapp.dto.AnniversaryDataDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class DateAnniversaryResponse {
    private Map<String, List<AnniversaryDataDTO>> data;
}
