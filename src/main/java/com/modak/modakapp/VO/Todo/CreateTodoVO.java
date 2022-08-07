package com.modak.modakapp.VO.Todo;

import com.modak.modakapp.domain.metadata.MDRepeatTag;
import lombok.Data;
import springfox.documentation.spring.web.json.Json;

import java.util.List;
import java.util.Map;

@Data
public class CreateTodoVO {
    private String accessToken;

    private String title;

    private String memo;

    private String date;

    private int memberId;

    private String timeTag;

    private String fromDate;

    private String toDate;

    private List<Integer> repeat;

}
