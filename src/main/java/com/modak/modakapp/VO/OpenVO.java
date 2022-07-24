package com.modak.modakapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@ApiModel(value = "Access, Refresh 토큰")
public class OpenVO {

    @ApiModelProperty(value = "Access 토큰")
    private String accessToken;

    @ApiModelProperty(value = "Refresh 토큰")
    private String refreshToken;

}