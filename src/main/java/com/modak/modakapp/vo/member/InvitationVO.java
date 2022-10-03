package com.modak.modakapp.vo.member;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

@Getter
@ApiModel(value = "초대 요청 데이터")
public class InvitationVO {
    private String invitationCode;
}
