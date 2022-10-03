package com.modak.modakapp.dto.message;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@ApiModel(value = "한 개의 메시지 정보")
public class MessageDTO {
    private int messageId;
    private int memberId;
    private String content;
    private Timestamp sendAt;
    private MetaDataDTO metaData;
}
