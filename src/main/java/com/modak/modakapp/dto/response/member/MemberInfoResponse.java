package com.modak.modakapp.dto.response.member;

import com.modak.modakapp.dto.FamilyMemberDTO;
import com.modak.modakapp.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MemberInfoResponse {
    MemberDTO memberResult;
    List<FamilyMemberDTO> familyMembersResult;
}
