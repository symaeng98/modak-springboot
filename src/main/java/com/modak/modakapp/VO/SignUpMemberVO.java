package com.modak.modakapp.VO;

import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Provider;
import com.modak.modakapp.domain.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SignUpMemberVO {

    private String name;

    private int year;
    private int month;
    private int day;

    private int isLunar;

    private String role;

    private String fcmToken;

    private String provider;

    private String providerId;

    public Member toMember() {
        return Member.builder()
                .name(name)
                .birthday(LocalDate.now())
                .is_lunar(1)
                .role(Role.DAD)
                .fcmToken("fcm damn")
                .provider(Provider.KAKAO)
                .providerId("provider id")
                .build();
    }

}
