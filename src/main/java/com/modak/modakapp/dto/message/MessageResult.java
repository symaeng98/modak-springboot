package com.modak.modakapp.dto.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MessageResult {
    private List<MessageDTO> result;
}
