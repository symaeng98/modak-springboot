package com.modak.modakapp.dto.member;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@ApiModel(value = "회원 정보")
public class MemberDTO {
    private int memberId;
    private int familyId;
    private String name;
    private int isLunar;
    private String birthday;
    private String profileImageUrl;
    private String role;
    private String color;
    private String provider;
    private String providerId;
    private List<String> tags;
    private List<MemberFamilyNameDTO> familyName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
