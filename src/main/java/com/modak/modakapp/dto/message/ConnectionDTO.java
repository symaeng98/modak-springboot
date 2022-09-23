package com.modak.modakapp.dto.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class ConnectionDTO {
    private int memberId;
    private Timestamp lastJoined;
    private boolean isJoining;
}
