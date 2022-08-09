package com.modak.modakapp.VO.Member;

import lombok.Data;

@Data
public class UpdateMemberVO {
    private String accessToken;
    private String name;
    private String role;
    private String color;
    private int isLunar;
    private String birthday;
}
