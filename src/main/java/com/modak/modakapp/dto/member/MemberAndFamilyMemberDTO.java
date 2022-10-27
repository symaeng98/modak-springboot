package com.modak.modakapp.dto.member;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ApiModel(value = "회원과 회원의 가족 구성원 정보")
public class MemberAndFamilyMemberDTO {
    String familyCode;
    MemberDTO memberResult;
    List<FamilyMemberDTO> familyMembersResult;
}
