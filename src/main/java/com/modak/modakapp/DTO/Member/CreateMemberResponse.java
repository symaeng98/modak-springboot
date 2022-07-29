package com.modak.modakapp.DTO.Member;

import com.modak.modakapp.DTO.CommonSuccessResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateMemberResponse {
    private int memberId;
    private int familyId;
}
