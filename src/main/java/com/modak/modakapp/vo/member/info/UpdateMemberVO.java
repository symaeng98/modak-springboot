package com.modak.modakapp.vo.member.info;

import lombok.Data;

@Data
public class UpdateMemberVO {
    private String name;
    private String role;
    private String color;
    private int isLunar;
    private String birthday;
}
