package com.modak.modakapp.vo.anniversary;

import com.modak.modakapp.domain.Anniversary;
import com.modak.modakapp.domain.enums.Category;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Date;

@Data
@ApiModel(value = "기념일 등록 및 수정 요청 데이터")
public class AnniversaryVO {
    @NotNull(message = "제목을 입력해주세요.")
    private String title;

    @NotNull(message = "날짜를 입력해주세요")
    private String date;

    @NotNull(message = "카테고리를 입력해주세요.")
    private String category;

    private String memo;

    private int isYear;

    private int isLunar;

    private String fromDate;

    private String toDate;

    public Anniversary toEntity() {
        return Anniversary.builder()
                .title(this.title)
                .startDate(Date.valueOf(this.date))
                .endDate(Date.valueOf(this.date))
                .category(Category.valueOf(this.category))
                .memo(this.memo)
                .isYear(this.isYear)
                .isLunar(this.isLunar)
                .build();
    }
}
