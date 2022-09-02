package com.modak.modakapp.vo.member.info;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

import java.util.List;

@Getter
@ApiModel(value = "회원의 개인태그 정보 요청 데이터")
public class UpdateMemberTagVO {
    private List<String> tags;
}