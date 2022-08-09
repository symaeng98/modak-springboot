package com.modak.modakapp.vo.member;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "로그인 정보", description = "Provider와 ProviderId를 가진 Dto")
public class LoginMemberVO {
    @ApiModelProperty(value = "프로바이더")
    private String provider;
    @ApiModelProperty(value = "프로바이더 아이디")
    private String providerId;
}
