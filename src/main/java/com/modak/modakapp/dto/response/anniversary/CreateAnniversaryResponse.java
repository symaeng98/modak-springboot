package com.modak.modakapp.dto.response.anniversary;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAnniversaryResponse {
    private int familyId;
    private int anniversaryId;
    private DateAnniversaryResponse updateLists;
}
