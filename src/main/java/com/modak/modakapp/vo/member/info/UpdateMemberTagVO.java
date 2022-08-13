package com.modak.modakapp.vo.member.info;

import lombok.Data;

import java.util.List;

@Data
public class UpdateMemberTagVO {
    private String accessToken;
    private List<String> tags;
}