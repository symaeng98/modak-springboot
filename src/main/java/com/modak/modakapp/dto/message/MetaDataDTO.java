package com.modak.modakapp.dto.message;

import com.modak.modakapp.dto.member.MemberMessageDTO;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@ApiModel(value = "메타 데이터")
public class MetaDataDTO {
    private String type_code;
    private List<String> key;
    private String count;

    // 룰렛
    private String title;
    private Boolean addTodo;
    private List<MemberMessageDTO> participatedUser;
    private MemberMessageDTO selectedUser;

    // 감정
    private String feeling;

    // 오는 길에
    private int step;

    // 주제 던지기
    private String quizType;
    private String hint;
}
