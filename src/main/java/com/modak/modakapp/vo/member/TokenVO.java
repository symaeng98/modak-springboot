package com.modak.modakapp.vo.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class TokenVO {
    @ApiModelProperty(value = "Access 토큰")
    private String accessToken;

    @ApiModelProperty(value = "Refresh 토큰")
    private String refreshToken;
}