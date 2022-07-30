package com.modak.modakapp.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommonFailResponse {
    private String message;
    private boolean status;
    private String code;



    @Builder
    public CommonFailResponse(String message, boolean status, String code) {
        this.message = message;
        this.status = status;
        this.code = code;
    }

    public static CommonFailResponse response(String message, String code){
        return CommonFailResponse.builder().message(message).status(false).code(code).build();
    }
}
