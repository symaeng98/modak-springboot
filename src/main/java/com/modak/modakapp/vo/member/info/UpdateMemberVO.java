package com.modak.modakapp.vo.member.info;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

@Getter
@ApiModel(value = "회원 정보 수정 요청 데이터")
public class UpdateMemberVO {
    private String name;

    private String role;

    private String color;

    private int isLunar;

    private String birthday;
}
