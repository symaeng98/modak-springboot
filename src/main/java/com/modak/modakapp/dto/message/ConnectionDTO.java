package com.modak.modakapp.dto.message;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@ApiModel(value = "한 명에 대한 연결 정보")
public class ConnectionDTO {
    private int memberId;
    private Timestamp lastJoined;
    private boolean isJoining;
}
