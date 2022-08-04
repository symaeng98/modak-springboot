package com.modak.modakapp.repository.date;

import com.modak.modakapp.DTO.Todo.DataDTO;
import com.modak.modakapp.DTO.Todo.WeekResponse;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.domain.TodoDone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.sql.Date;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class TodoDateRepository {

    private final EntityManager em;

    // 쿼리 2번 날리는 버전 - 색깔, todo 따로

//    public Map<String,List<String>> findWeekColorsByDateRange(List<String> dates){
//        Map<String,List<String>> result = new HashMap<>();
//        for (String date : dates) {
//            List<String> colorList = new ArrayList<>();
//            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
//            List<Todo> todoList = em.createQuery("select t from Todo t"
//                                    + " where t.startDate <= :date and t.endDate >= :date"
//                            , Todo.class)
//                    .setParameter("date", sqlDate)
//                    .getResultList();
//            todoList.forEach(t -> {
//                Member member = t.getMember();
//                String color = member.getColor();
//                colorList.add(color);
//            });
//            // 중복 제거
//            Set<String> set = new HashSet<String>(colorList);
//            List<String> newList = new ArrayList<String>(set);
//            result.put(date,newList);
//        }
//        // 정렬
//        return new TreeMap<>(result);
//    }

//    public Map<String, List<DataDTO>> findWeekItemsByDateRange(List<String> dates){
//        Map<String,List<DataDTO>> result = new HashMap<>();
//        for (String date : dates) {
//            List<DataDTO> dataDTOList = new ArrayList<>();
//            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
//            List<Todo> todoList = em.createQuery("select t from Todo t"
//                                    + " where t.startDate <= :date and t.endDate >= :date"
//                            , Todo.class)
//                    .setParameter("date", sqlDate)
//                    .getResultList();
//            todoList.forEach(t -> {
//                Member member = t.getMember();
//                try {
//                    int isDone = t.getTodoDone().getIsDone();
//                    DataDTO dataDto = DataDTO.builder().todoId(t.getId())
//                            .title(t.getTitle())
//                            .memo(t.getMemo())
//                            .timeTag(t.getTimeTag())
//                            .repeatTag(t.getRepeatTag())
//                            .color(member.getColor())
//                            .memberId(member.getId())
//                            .isDone(isDone)
//                            .build();
//                    dataDTOList.add(dataDto);
//                }
//                catch (Exception e){
//                    DataDTO dataDto = DataDTO.builder().todoId(t.getId())
//                            .title(t.getTitle())
//                            .memo(t.getMemo())
//                            .timeTag(t.getTimeTag())
//                            .repeatTag(t.getRepeatTag())
//                            .color(member.getColor())
//                            .memberId(member.getId())
//                            .isDone(0)
//                            .build();
//                    dataDTOList.add(dataDto);
//                }
//                result.put(date,dataDTOList);
//            });
//
//        }
//        // 정렬
//        return new TreeMap<>(result);
//    }

    // 색깔, todo 한 번에 가져오기
    // 추후 수정...
    public WeekResponse findWeekColorsAndItemsByDateRange(List<String> dates){
        Map<String,List<String>> result1 = new HashMap<>();
        Map<String,List<DataDTO>> result2 = new HashMap<>();
        for (String date : dates) {
            List<String> colorList = new ArrayList<>();
            List<DataDTO> dataDTOList = new ArrayList<>();
            Date sqlDate = Date.valueOf(date);
            List<Todo> todoList = em.createQuery("select t from Todo t"
                                    + " where t.startDate <= :date and t.endDate >= :date"
                            , Todo.class)
                    .setParameter("date", sqlDate)
                    .getResultList();
            todoList.forEach(t -> {
                Member member = t.getMember();
                String color = member.getColor();
                colorList.add(color);
                try {
                    int isDone = t.getTodoDone().getIsDone();
                    DataDTO dataDto = DataDTO.builder().todoId(t.getId())
                            .title(t.getTitle())
                            .memo(t.getMemo())
                            .timeTag(t.getTimeTag())
                            .repeatTag(t.getRepeatTag())
                            .color(member.getColor())
                            .memberId(member.getId())
                            .isDone(isDone)
                            .build();
                    dataDTOList.add(dataDto);
                }
                catch (NullPointerException e){
                    DataDTO dataDto = DataDTO.builder().todoId(t.getId())
                            .title(t.getTitle())
                            .memo(t.getMemo())
                            .timeTag(t.getTimeTag())
                            .repeatTag(t.getRepeatTag())
                            .color(member.getColor())
                            .memberId(member.getId())
                            .isDone(0)
                            .build();
                    dataDTOList.add(dataDto);
                }
                result1.put(date,colorList);
                result2.put(date,dataDTOList);
            });
            // 중복 제거
            Set<String> set = new HashSet<String>(colorList);
            List<String> newList = new ArrayList<String>(set);
            result1.put(date,newList);
        }
        // 정렬
        TreeMap<String, List<String>> treeResult1 = new TreeMap<>(result1);
        TreeMap<String, List<DataDTO>> treeResult2 = new TreeMap<>(result2);
        return new WeekResponse(treeResult1,treeResult2);
    }

}
