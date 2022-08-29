package com.modak.modakapp.vo.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class AccessTokenVO {
    @ApiModelProperty(value = "Access 토큰")
    private String accessToken;
}
