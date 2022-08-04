package com.modak.modakapp.repository.date;

import com.modak.modakapp.DTO.Todo.DataDTO;
import com.modak.modakapp.DTO.Todo.WeekResponse;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.domain.TodoDone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TodoDateRepository {

    private final EntityManager em;

    // 색깔, todo 한 번에 가져오기
    // 추후 수정...
    public WeekResponse findWeekColorsAndItemsByDateRange(List<String> dates, Family family){
        Map<String,List<String>> result1 = new HashMap<>();
        Map<String,List<DataDTO>> result2 = new HashMap<>();
        String firstDate = dates.get(0);
        String lastDate = dates.get(dates.size()-1);
        Date sqlFirstDate = Date.valueOf(firstDate);
        Date sqlLastDate = Date.valueOf(lastDate);
        List<Todo> todoList = em.createQuery("select t from Todo t"+
                                " where ((t.startDate <= :firstDate and :firstDate <= t.endDate)" +
                                " or (:firstDate<=t.startDate and t.endDate <= :lastDate)" +
                                " or (t.startDate<=:lastDate and :lastDate <= t.endDate))" +
                                " and t.family.id = :familyId"
                        , Todo.class)
                .setParameter("firstDate", sqlFirstDate)
                .setParameter("lastDate",sqlLastDate)
                .setParameter("familyId",family.getId())
                .getResultList();

        for (String date : dates) {
            List<String> colorList = new ArrayList<>();
            List<DataDTO> dataDTOList = new ArrayList<>();
            for (Todo t : todoList) {
                Date startDate = t.getStartDate();
                Date endDate = t.getEndDate();
                if (isValid(startDate, endDate, Date.valueOf(date))&&t.getId()!=t.getGroupTodoId()) { //수정된 애들 먼저 넣기
                    Member member = t.getMember();
                    String color = member.getColor();
                    colorList.add(color);

                    int isDone = getIsDone(t, Date.valueOf(date));
                    DataDTO dataDto = DataDTO.builder().todoId(t.getId())
                            .title(t.getTitle())
                            .memo(t.getMemo())
                            .timeTag(t.getTimeTag())
                            .repeatTag(t.getRepeatTag())
                            .color(member.getColor())
                            .memberId(member.getId())
                            .isDone(isDone)
                            .groupTodoId(t.getGroupTodoId())
                            .build();
                    dataDTOList.add(dataDto);
                }
            }
            result1.put(date,colorList);
            result2.put(date,dataDTOList);
        }

        // 반복 to-do 삽입
        for (String date : dates) {
            List<String> res1 = result1.get(date);
            List<DataDTO> res2 = result2.get(date);
            for (Todo t : todoList) {
                Date startDate = t.getStartDate();
                Date endDate = t.getEndDate();
                if (isValid(startDate, endDate, Date.valueOf(date))&&t.getId()==t.getGroupTodoId()) {
                    List<DataDTO> dataDTOs = result2.get(date);
                    List<DataDTO> collect = dataDTOs.stream().filter(d -> d.getGroupTodoId() == t.getGroupTodoId()).collect(Collectors.toList());
                    if(collect.size()==1){ // 이미 같은 그룹 들어있으면
                        System.out.println(date);
                        continue;
                    }
                    Member member = t.getMember();
                    String color = member.getColor();
                    res1.add(color);

                    int isDone = getIsDone(t, Date.valueOf(date));
                    DataDTO dataDto = DataDTO.builder().todoId(t.getId())
                            .title(t.getTitle())
                            .memo(t.getMemo())
                            .timeTag(t.getTimeTag())
                            .repeatTag(t.getRepeatTag())
                            .color(member.getColor())
                            .memberId(member.getId())
                            .isDone(isDone)
                            .groupTodoId(t.getGroupTodoId())
                            .build();
                    res2.add(dataDto);
                }
            }
            // 중복 제거
            Set<String> set = new HashSet<String>(res1);
            List<String> newList = new ArrayList<String>(set);

            result1.put(date,newList);
            result2.put(date,res2);

        }
        // 정렬
        TreeMap<String, List<String>> treeResult1 = new TreeMap<>(result1);
        TreeMap<String, List<DataDTO>> treeResult2 = new TreeMap<>(result2);
        return new WeekResponse(treeResult1,treeResult2);
    }

    public int getIsDone(Todo todo, Date date){
        List<TodoDone> todoDones = todo.getTodoDone();
        if(todoDones.size()==0){
            return 0;
        }
        List<TodoDone> todoDoneList = todoDones.stream().filter(t -> t.getDate().equals(date)).collect(Collectors.toList());
        if(todoDoneList.size()==0){
            return 0;
        }
        TodoDone todoDone = todoDoneList.get(0);
        return todoDone.getIsDone();
    }


    public boolean isValid(Date start, Date end, Date date){
        if(start.equals(date)) return true;
        else if(start.before(date)){
            if(end.before(date)) return false;
            else return true;
        } else return false;
    }
}
