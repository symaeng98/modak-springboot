package com.modak.modakapp.dto.message;

import com.modak.modakapp.dto.metadata.MetaData;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class MessageDTO {
    private int user_id;
    private String content;
    private Timestamp send_at;
    private MetaData metadata;
}
