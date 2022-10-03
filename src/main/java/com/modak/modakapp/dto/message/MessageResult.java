package com.modak.modakapp.dto.message;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@ApiModel(value = "메시지 정보")
public class MessageResult {
    private int lastId;
    private List<MessageDTO> result;
}
