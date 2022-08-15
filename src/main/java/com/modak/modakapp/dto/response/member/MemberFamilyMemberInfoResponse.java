package com.modak.modakapp.dto.response.member;

import com.modak.modakapp.dto.MemberFamilyMemberDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MemberFamilyMemberInfoResponse {
    List<MemberFamilyMemberDTO> membersData;
}
