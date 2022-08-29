package com.modak.modakapp.dto.response.member;

import com.modak.modakapp.dto.FamilyMemberDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FamilyMemberInfoResponse {
    List<FamilyMemberDTO> membersData;
}
