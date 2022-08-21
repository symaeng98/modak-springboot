package com.modak.modakapp.repository.date;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.domain.TodoDone;
import com.modak.modakapp.dto.TodoDTO;
import com.modak.modakapp.dto.response.todo.WeekResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class TodoDateRepository {

    private final EntityManager em;

    // 색깔, to-do 한 번에 가져오기
    // 추후 수정...
    public WeekResponse findWeekColorsAndItemsAndGaugeByDateRange(String sd, String ed, Family family) {

        List<String> dates = new ArrayList<>();
        LocalDate start = LocalDate.parse(sd);
        LocalDate end = LocalDate.parse(ed);
        Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .forEach(d -> {
                    dates.add(String.valueOf(d));
                });

        Map<String, List<String>> result1 = new HashMap<>();
        Map<String, List<TodoDTO>> result2 = new HashMap<>();
        String firstDate = dates.get(0);
        String lastDate = dates.get(dates.size() - 1);
        Date sqlFirstDate = Date.valueOf(firstDate);
        Date sqlLastDate = Date.valueOf(lastDate);
        List<Todo> todoList = em.createQuery("select t from Todo t" +
                                " where ((t.startDate <= :firstDate and :firstDate <= t.endDate)" +
                                " or (:firstDate<=t.startDate and t.endDate <= :lastDate)" +
                                " or (t.startDate<=:lastDate and :lastDate <= t.endDate))" +
                                " and t.family.id = :familyId" +
                                " and t.deletedAt is null"
                        , Todo.class)
                .setParameter("firstDate", sqlFirstDate)
                .setParameter("lastDate", sqlLastDate)
                .setParameter("familyId", family.getId())
                .getResultList();


        for (String date : dates) {
            List<String> colorList = new ArrayList<>();
            List<TodoDTO> todoDTOList = new ArrayList<>();
            for (Todo t : todoList) {
                Date startDate = t.getStartDate();
                Date endDate = t.getEndDate();
                if (isValid(startDate, endDate, Date.valueOf(date))) {
                    if (t.getRepeatTag() != null) { // 반복이면
                        if (!isValidDateAndDay(Date.valueOf(date), t)) {
                            continue;
                        }
                    }
                    Member member = t.getMember();
                    String color = member.getColor();
                    colorList.add(color);

                    int isDone = getIsDone(t, Date.valueOf(date));
                    TodoDTO todoDto = TodoDTO.builder().todoId(t.getId())
                            .title(t.getTitle())
                            .memo(t.getMemo())
                            .timeTag(t.getTimeTag())
                            .repeatTag(t.getRepeatTag())
                            .color(member.getColor())
                            .memberId(member.getId())
                            .isDone(isDone)
                            .groupTodoId(t.getGroupTodoId())
                            .build();
                    todoDTOList.add(todoDto);
                }
            }
            // 중복 제거
            Set<String> set = new HashSet<String>(colorList);
            List<String> newList = new ArrayList<String>(set);

            result1.put(date, newList);
            result2.put(date, todoDTOList);
        }

        TreeMap<String, List<String>> treeResult1 = new TreeMap<>(result1);
        TreeMap<String, List<TodoDTO>> treeResult2 = new TreeMap<>(result2);
        int gauge = findNumOfDone(family);

        return new WeekResponse(treeResult1, treeResult2, gauge);
    }


    public WeekResponse getCreateResponse(Todo t, List<String> dates, Family family) {
        Map<String, List<String>> result1 = new HashMap<>();
        Map<String, List<TodoDTO>> result2 = new HashMap<>();
        String firstDate = dates.get(0);
        String lastDate = dates.get(dates.size() - 1);
        Date sqlFirstDate = Date.valueOf(firstDate);
        Date sqlLastDate = Date.valueOf(lastDate);

        // 반복 - 단일 수정 삽입
        for (String date : dates) {
            List<String> colorList = new ArrayList<>();
            List<TodoDTO> todoDTOList = new ArrayList<>();

            Date startDate = t.getStartDate();
            Date endDate = t.getEndDate();
            if (isValid(startDate, endDate, Date.valueOf(date))) {
                if (t.getRepeatTag() == null) { // 단일
                    Member member = t.getMember();
                    String color = member.getColor();
                    colorList.add(color);

                    TodoDTO todoDto = TodoDTO.builder().todoId(t.getId())
                            .title(t.getTitle())
                            .memo(t.getMemo())
                            .timeTag(t.getTimeTag())
                            .repeatTag(t.getRepeatTag())
                            .color(member.getColor())
                            .memberId(member.getId())
                            .isDone(0)
                            .groupTodoId(t.getGroupTodoId())
                            .build();
                    todoDTOList.add(todoDto);
                } else { // 반복
                    if (isValidDateAndDay(Date.valueOf(date), t)) { // 요일이 날짜에 해당하면
                        Member member = t.getMember();
                        String color = member.getColor();
                        colorList.add(color);

                        TodoDTO todoDto = TodoDTO.builder().todoId(t.getId())
                                .title(t.getTitle())
                                .memo(t.getMemo())
                                .timeTag(t.getTimeTag())
                                .repeatTag(t.getRepeatTag())
                                .color(member.getColor())
                                .memberId(member.getId())
                                .isDone(0)
                                .groupTodoId(t.getGroupTodoId())
                                .build();
                        todoDTOList.add(todoDto);
                    } else {
                        continue;
                    }
                }
                // 중복 제거
                Set<String> set = new HashSet<String>(colorList);
                List<String> newList = new ArrayList<String>(set);

                result1.put(date, newList);
                result2.put(date, todoDTOList);
            }

        }
        // 정렬
        TreeMap<String, List<String>> treeResult1 = new TreeMap<>(result1);
        TreeMap<String, List<TodoDTO>> treeResult2 = new TreeMap<>(result2);
        int gauge = findNumOfDone(family);

        return new WeekResponse(treeResult1, treeResult2, gauge);
    }


    public int findNumOfDone(Family family) {
        Long num = em.createQuery("select count(t) from TodoDone t where t.family.id = :id and t.isDone=1 and t.deletedAt is null", Long.class)
                .setParameter("id", family.getId()).getSingleResult();
        return num.intValue();
    }


    public int getIsDone(Todo todo, Date date) {
        List<TodoDone> todoDones = todo.getTodoDone();
        if (todoDones == null) {
            return 0;
        }
        List<TodoDone> todoDoneList = todoDones.stream().filter(t -> t.getDate().equals(date)).collect(Collectors.toList());
        if (todoDoneList.size() == 0) {
            return 0;
        }
        TodoDone todoDone = todoDoneList.get(0);
        return todoDone.getIsDone();
    }

    // 현재 날짜가 todo의 반복 요일에 포함이 되는지 확인
    public boolean isValidDateAndDay(Date date, Todo todo) {
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

    public boolean isValid(Date start, Date end, Date date) {
        if (start.after(end)) return false;
        if (start.equals(date)) return true;
        else if (start.before(date)) {
            return !end.before(date);
        } else return false;
    }
}
