package com.modak.modakapp.VO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class UserVO {

    private String name;

    private int year;
    private int month;
    private int day;

    private int isLunar;

    private String role;

    private String fcmToken;

    private String provider;

    private String providerId;

}
