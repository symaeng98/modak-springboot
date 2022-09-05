package com.modak.modakapp.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommonSuccessResponse<T> {
    private String message;
    private T data;
    private boolean status;

    public CommonSuccessResponse(String message, T data, boolean status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }
}
