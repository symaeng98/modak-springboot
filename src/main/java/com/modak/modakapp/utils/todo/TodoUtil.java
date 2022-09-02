package com.modak.modakapp.utils.todo;

import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.domain.TodoDone;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class TodoUtil {
    public int getIsDone(Todo todo, Date date) {
        List<TodoDone> todoDoneList = todo.getTodoDone();
        if (todoDoneList.size() == 0) {
            return 0;
        }
        List<TodoDone> todoDoneFilterList = todoDoneList.stream().filter(t -> t.getDate().equals(date)).collect(Collectors.toList());
        if (todoDoneFilterList.size() == 0) {
            return 0;
        }
        TodoDone todoDone = todoDoneFilterList.get(0);
        return todoDone.getIsDone();
    }

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

    // 현재 날짜가 todo의 반복 요일에 포함이 되는지 확인
    public boolean isTodoDayOfWeekIncludesCurrentDate(Date date, Todo todo) {
        String repeatTag = todo.getRepeatTag();
        if (repeatTag == null) return true;
        if (repeatTag.equals("매일")) return true;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        String day = "";
        switch (dayOfWeek) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;
        }
        if (repeatTag.contains(day)) return true;
        if (day.equals("토") || day.equals("일")) { // 토 or 일이면
            if (repeatTag.equals("주중")) return false;
            return repeatTag.equals("주말");
        } else { // 평일이면
            return repeatTag.contains("주중");
        }
    }

    public boolean isCurrentDateValidByStartDateAndEndDate(Date start, Date end, Date date) {
        if (start.equals(date)) return true;
        else if (start.before(date)) {
            return !end.before(date);
        } else return false;
    }

    public List<String> getFromToDateList(String fromDate, String toDate) {
        List<String> dates = new ArrayList<>();
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);
        Stream.iterate(from, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(from, to) + 1)
                .forEach(d -> {
                    dates.add(String.valueOf(d));
                });
        return dates;
    }
}