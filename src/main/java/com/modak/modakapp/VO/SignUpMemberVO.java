package com.modak.modakapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "회원 정보",description = "이름, 생년월일, 음력유무, 역할, fcm 토큰, 프로바이더, 프로바이더 아이디를 가진 클래스")
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
