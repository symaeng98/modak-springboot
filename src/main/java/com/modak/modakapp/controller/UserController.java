package com.modak.modakapp.controller;

import com.modak.modakapp.VO.UserVO;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Provider;
import com.modak.modakapp.domain.Role;
import com.modak.modakapp.domain.User;
import com.modak.modakapp.service.FamilyService;
import com.modak.modakapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FamilyService familyService;

    @PostMapping("/users/new")
    public String create(@RequestBody UserVO userVO){
        Family family = new Family();
        family.setName("행복한 우리 가족");
        int joinFamilyId = familyService.join(family);

        User user = new User();

        user.setFamily(family);

        user.setName(userVO.getName());

        user.setIs_lunar(userVO.getIsLunar());
        // 생일 로직
        // sdfsd
        user.setBirthday(LocalDate.now());
        // Role
        if(userVO.getRole().equals("DAD")){
            user.setRole(Role.DAD);
        }
        else if(userVO.getRole().equals("MOM")){
            user.setRole(Role.MOM);
        }
        else if(userVO.getRole().equals("SON")){
            user.setRole(Role.SON);
        }
        else{
            user.setRole(Role.DAU);
        }

        // Provider
        if(userVO.getProvider().equals("KAKAO")){
            user.setProvider(Provider.KAKAO);
        }
        else{
            user.setProvider(Provider.APPLE);
        }

        // ProviderId
        user.setProviderId("프로바이더 아이디임ㅋ");

        // chatLastJoined
        user.setChatLastJoined(LocalDateTime.now());

        // chatNowJoining
        user.setChatNowJoining(0);

        // Refresh Token
        user.setRefreshToken("리프레시임ㅋ");

        // FCM Token
        user.setFcmToken("FCM임ㅋ");

        // 저장
        userService.join(user);

        return "woi";
    }


}