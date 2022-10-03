package com.modak.modakapp.vo.member;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "회원가입 정보 요청 데이터")
public class SignUpMemberVO {
    @ApiModelProperty(value = "이름")
    private String name;

    @ApiModelProperty(value = "생년월일")
    private String birthday;

    @ApiModelProperty(value = "음력")
    private int isLunar;

    @ApiModelProperty(value = "역할")
    private String role;

    @ApiModelProperty(value = "fcm 토큰")
    private String fcmToken;

    @ApiModelProperty(value = "프로바이더")
    private String provider;

    @ApiModelProperty(value = "프로바이더 아이디")
    private String providerId;
}
