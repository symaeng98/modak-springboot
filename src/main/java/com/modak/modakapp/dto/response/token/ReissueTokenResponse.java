package com.modak.modakapp.dto.response.token;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(value = "토큰 재발급 응답 정보")
public class ReissueTokenResponse {
    private String type;
}