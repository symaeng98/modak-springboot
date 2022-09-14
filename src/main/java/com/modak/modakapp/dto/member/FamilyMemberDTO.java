package com.modak.modakapp.dto.member;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
