package com.modak.modakapp.dto.member;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "회원의 가족 구성원 한 명에 대한 정보")
public class FamilyMemberDTO {
    private int memberId;
    private String name;
    private int isLunar;
    private String birthday;
    private String profileImageUrl;
    private String role;
    private String color;

    @Builder
    public FamilyMemberDTO(
            int memberId,
            String name,
            int isLunar,
            String birthday,
            String profileImageUrl,
            String role,
            String color
    ) {
        this.memberId = memberId;
        this.name = name;
        this.isLunar = isLunar;
        this.birthday = birthday;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.color = color;
    }
}
