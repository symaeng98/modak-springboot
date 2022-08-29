package com.modak.modakapp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FamilyMemberDTO {
    private int id;
    private String name;
    private int isLunar;
    private String birthday;
    private String profileImageUrl;
    private String role;
    private String color;

    @Builder
    public FamilyMemberDTO(
            int id,
            String name,
            int isLunar,
            String birthday,
            String profileImageUrl,
            String role,
            String color
    ) {
        this.id = id;
        this.name = name;
        this.isLunar = isLunar;
        this.birthday = birthday;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.color = color;
    }
}
