package com.modak.modakapp.dto.member;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberMessageDTO {
    private String name;
    private String color;
}
