package com.modak.modakapp.vo.member;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@ApiModel(value = "초대 요청 데이터")
public class InvitationVO {
    @NotNull(message = "초대 코드를 입력해주세요.")
    private String invitationCode;
}
