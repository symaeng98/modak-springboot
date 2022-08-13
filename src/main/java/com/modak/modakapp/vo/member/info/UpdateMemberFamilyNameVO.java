package com.modak.modakapp.vo.member.info;

import com.modak.modakapp.dto.MemberFamilyNameDTO;
import lombok.Data;

import java.util.List;
@Data
public class UpdateMemberFamilyNameVO {
    private String accessToken;
    private List<MemberFamilyNameDTO> memberFamilyName;
}
