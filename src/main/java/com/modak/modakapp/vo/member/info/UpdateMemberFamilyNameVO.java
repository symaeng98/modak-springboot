package com.modak.modakapp.vo.member.info;

import com.modak.modakapp.dto.MemberFamilyNameDTO;
import lombok.Data;

import java.util.List;

@Data
public class UpdateMemberFamilyNameVO {
    private List<MemberFamilyNameDTO> memberFamilyName;
}
