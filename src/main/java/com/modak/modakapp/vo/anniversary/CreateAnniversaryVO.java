package com.modak.modakapp.vo.anniversary;

import lombok.Data;

@Data
public class CreateAnniversaryVO {
    private String title;
    private String date;
    private String category;
    private String memo;
    private int isYear;
    private int isLunar;
    private String fromDate;
    private String toDate;
}
