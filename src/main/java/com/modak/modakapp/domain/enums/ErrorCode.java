package com.modak.modakapp.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    EXPIRED_TOKEN("만료된 토큰입니다.", false, "ExpiredJwtException"),
    INVALID_TOKEN("유효하지 않은 토큰입니다. JWT 포맷이 올바른지 확인하세요.", false, "JwtException"),
    NO_TOKEN("헤더에 토큰이 없습니다.", false, "NullPointerException"),
    ;

    private final String message;
    private final boolean status;
    private final String code;
}