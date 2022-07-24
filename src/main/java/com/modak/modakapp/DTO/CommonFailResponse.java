package com.modak.modakapp.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommonFailResponse {
    private String message;
    private boolean status;


    @Builder
    public CommonFailResponse(String message, boolean status) {
        this.message = message;
        this.status = status;
    }

    public static CommonFailResponse response(String message){
        return CommonFailResponse.builder().message(message).status(false).build();
    }
}
