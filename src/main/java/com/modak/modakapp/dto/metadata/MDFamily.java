package com.modak.modakapp.dto.metadata;


import com.modak.modakapp.dto.MemberFamilyNameDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MDFamily {
    private List<MemberFamilyNameDTO> memberFamilyName;
}
