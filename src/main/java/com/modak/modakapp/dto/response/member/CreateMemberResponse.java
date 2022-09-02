package com.modak.modakapp.dto.response.member;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(value = "회원 가입 응답", description = "회원 id, 가족 id")
public class CreateMemberResponse {
    private int memberId;
    private int familyId;
}
