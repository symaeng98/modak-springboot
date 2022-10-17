package com.modak.modakapp.vo.member.info;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@ApiModel(value = "회원 정보 수정 요청 데이터")
public class UpdateMemberVO {
    @NotNull(message = "이름을 입력해주세요.")
    private String name;

    @NotNull(message = "역할을 입력해주세요.")
    private String role;

    @NotNull(message = "색깔을 입력해주세요.")
    private String color;

    @NotNull(message = "음력인지 아닌지를 입력해주세요.")
    private int isLunar;

    @NotNull(message = "생일을 입력해주세요.")
    private String birthday;
}
