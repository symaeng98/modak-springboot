package com.modak.modakapp.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MemberAndFamilyMemberDTO {
    MemberDTO memberResult;
    List<FamilyMemberDTO> familyMembersResult;
}
