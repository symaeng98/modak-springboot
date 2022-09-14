package com.modak.modakapp.dto.metadata;


import com.modak.modakapp.dto.member.MemberFamilyNameDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MDFamily {
    private List<MemberFamilyNameDTO> memberFamilyName;
}
