package com.modak.modakapp.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommonSuccessResponse<T> {
    private String message;
    private T data;
    private boolean status;

    @Builder
    public CommonSuccessResponse(String message, T data, boolean status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public static CommonSuccessResponse response(String message, Object data) {
        return CommonSuccessResponse.builder().message(message).data(data).status(true).build();
    }

    public static CommonSuccessResponse successResponse(String message) {
        return CommonSuccessResponse.builder().message(message).status(true).build();
    }
}
