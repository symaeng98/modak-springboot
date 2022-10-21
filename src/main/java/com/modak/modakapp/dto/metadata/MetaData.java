package com.modak.modakapp.dto.metadata;

import com.modak.modakapp.dto.member.MemberMessageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaData {
    private String type_code;
    private List<String> key;
    private String count;

    // 룰렛
    private String title;
    private Boolean addTodo;
    private List<MemberMessageDTO> participatedUsers;
    private MemberMessageDTO selectedUser;

    // 감정
    private String feeling;

    // 오는 길에
    private int step;

    // 주제 던지기
    private String quizType;
    private String hint;
}
