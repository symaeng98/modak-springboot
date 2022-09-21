package com.modak.modakapp.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MemberAndFamilyMemberDTO {
    String familyCode;
    MemberDTO memberResult;
    List<FamilyMemberDTO> familyMembersResult;
}
