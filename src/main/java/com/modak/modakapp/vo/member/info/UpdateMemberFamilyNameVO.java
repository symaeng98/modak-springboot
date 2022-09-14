package com.modak.modakapp.vo.member.info;

import com.modak.modakapp.dto.member.MemberFamilyNameDTO;
import io.swagger.annotations.ApiModel;
import lombok.Getter;

import java.util.List;

@Getter
@ApiModel(value = "회원의 가족 구성원 이름정보 수정 요청 데이터")
public class UpdateMemberFamilyNameVO {
    private List<MemberFamilyNameDTO> memberFamilyName;
}
