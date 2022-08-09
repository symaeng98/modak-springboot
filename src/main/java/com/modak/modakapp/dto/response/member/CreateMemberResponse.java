package com.modak.modakapp.dto.response.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateMemberResponse {
    private int memberId;
    private int familyId;
    private int anniversaryId;
}
