package com.modak.modakapp.vo.member;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Access, Refresh 토큰")
public class OpenVO {

    @ApiModelProperty(value = "Access 토큰")
    private String accessToken;

    @ApiModelProperty(value = "Refresh 토큰")
    private String refreshToken;

}