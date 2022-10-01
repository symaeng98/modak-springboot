package com.modak.modakapp.dto.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class MessageDTO {
    private int messageId;
    private int memberId;
    private String content;
    private Timestamp sendAt;
    private MetaDataDTO metaData;
}