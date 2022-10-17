package com.modak.modakapp.vo.member;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@ApiModel(value = "회원가입 정보 요청 데이터")
public class SignUpMemberVO {
    @NotNull(message = "이름을 입력해주세요.")
    @ApiModelProperty(value = "이름")
    private String name;

    @NotNull(message = "생년월일을 입력해주세요.")
    @ApiModelProperty(value = "생년월일")
    private String birthday;

    @NotNull(message = "음력인지 아닌지를 입력해주세요.")
    @ApiModelProperty(value = "음력")
    private int isLunar;

    @NotNull(message = "역할을 입력해주세요.")
    @ApiModelProperty(value = "역할")
    private String role;

    @ApiModelProperty(value = "fcm 토큰")
    private String fcmToken;

    @NotNull(message = "프로바이더를 입력해주세요.")
    @ApiModelProperty(value = "프로바이더")
    private String provider;

    @NotNull(message = "프로바이더 아이디를 입력해주세요.")
    @ApiModelProperty(value = "프로바이더 아이디")
    private String providerId;
}
