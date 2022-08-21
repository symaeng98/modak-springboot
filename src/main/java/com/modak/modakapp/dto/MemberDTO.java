package com.modak.modakapp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class MemberDTO {
    private int id;
    private int familyId;
    private String name;
    private int isLunar;
    private String birthDay;
    private String profileImageUrl;
    private String role;
    private String color;
    private String provider;
    private String providerId;
    private List<String> tags;
    private List<MemberFamilyNameDTO> familyName;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    @Builder
    public MemberDTO(int id, int familyId, String name, int isLunar, String birthDay, String profileImageUrl, String role, String color, String provider, String providerId, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.familyId = familyId;
        this.name = name;
        this.isLunar = isLunar;
        this.birthDay = birthDay;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.color = color;
        this.provider = provider;
        this.providerId = providerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
