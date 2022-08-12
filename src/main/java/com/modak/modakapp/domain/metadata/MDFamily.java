package com.modak.modakapp.domain.metadata;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MDFamily {
    private Map<Integer,String> familyMemberNames;
}
