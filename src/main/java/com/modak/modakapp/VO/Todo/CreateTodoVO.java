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

    private String describe;

    private String startDate;

    private String endDate;

    private String timeTag;

    private Map<String,List<String>> repeatTag;

}
