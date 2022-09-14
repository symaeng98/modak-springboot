package com.modak.modakapp.dto.member;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

@Getter
@ApiModel(value = "가족 구성원 데이터", description = "회원이 설정한 가족 구성원(id) 이름 정보")
public class MemberFamilyNameDTO {
    private int id;
    private String name;
}
