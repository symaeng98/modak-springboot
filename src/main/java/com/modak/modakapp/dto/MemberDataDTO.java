package com.modak.modakapp.dto;

import com.modak.modakapp.domain.metadata.MDFamily;
import com.modak.modakapp.domain.metadata.MDTag;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Getter
public class MemberDataDTO {
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
    private MDTag tags;
    private MDFamily familyName;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    @Builder

    public MemberDataDTO(int id, int familyId, String name, int isLunar, String birthDay, String profileImageUrl, String role, String color, String provider, String providerId, MDTag tags, MDFamily familyName, Timestamp createdAt, Timestamp updatedAt) {
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
        this.tags = tags;
        this.familyName = familyName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
