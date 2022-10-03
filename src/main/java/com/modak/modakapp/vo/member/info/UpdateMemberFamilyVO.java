package com.modak.modakapp.vo.member.info;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

@Getter
@ApiModel(value = "한 개의 기념일에 대한 정보")
public class UpdateMemberFamilyVO {
    private int familyId;
}
