package com.modak.modakapp.dto.response.anniversary;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(value = "생성, 수정, 삭제 시의 기념일 id 및 날짜 별로 요청한 기념일 정보 list")
public class AnniversaryResponse {
    private int familyId;
    private int anniversaryId;
    private DateAnniversaryResponse updateLists;
}
