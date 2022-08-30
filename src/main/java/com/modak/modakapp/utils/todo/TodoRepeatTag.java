package com.modak.modakapp.utils.todo;

import java.util.List;

public class TodoRepeatTag {
    public String getRepeatTag(List<Integer> repeat) {
        String[] day = {"일", "월", "화", "수", "목", "금", "토"};

        String repeatTag = "";
        if (repeat.get(0) == 0 && repeat.get(1) == 0 && repeat.get(2) == 0 && repeat.get(3) == 0 && repeat.get(4) == 0 && repeat.get(5) == 0 && repeat.get(6) == 0) {
            return null;
        }

        // 주중
        if (repeat.get(1) == 1 && repeat.get(2) == 1 && repeat.get(3) == 1 && repeat.get(4) == 1 && repeat.get(5) == 1) {
            repeatTag = "주중";
            if (repeat.get(0) == 1 && repeat.get(6) == 1) {
                repeatTag = "매일";
            } else if (repeat.get(0) == 1) {
                repeatTag += ", 일";
            } else if (repeat.get(6) == 1) {
                repeatTag += ", 토";
            } else {
                return repeatTag;
            }
        } else if (repeat.get(1) == 0 && repeat.get(2) == 0 && repeat.get(3) == 0 && repeat.get(4) == 0 && repeat.get(5) == 0 && repeat.get(0) == 1 && repeat.get(6) == 1) {
            repeatTag = "주말";
        } else {
            int i = 0;
            for (Integer r : repeat) {
                if (r == 1) {
                    repeatTag = repeatTag + day[i] + ", ";
                }
                i++;
            }
            return repeatTag.substring(0, repeatTag.length() - 2);
        }
        return repeatTag;
    }
}