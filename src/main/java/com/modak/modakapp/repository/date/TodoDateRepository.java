package com.modak.modakapp.repository.date;

import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class TodoDateRepository {

    private final EntityManager em;

    public Map<String,List<String>> findWeekColorsByDateRange(List<String> dates){
        Map<String,List<String>> result = new HashMap<>();
        for (String date : dates) {
            List<String> colorList = new ArrayList<>();
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            List<Todo> todoList = em.createQuery("select t from Todo t"
                                    + " where t.startDate <= :date and t.endDate >= :date"
                            , Todo.class)
                    .setParameter("date", sqlDate)
                    .getResultList();
            todoList.forEach(t -> {
                Member member = t.getMember();
                String color = member.getColor();
                colorList.add(color);
            });
            // 중복 제거
            Set<String> set = new HashSet<String>(colorList);
            List<String> newList = new ArrayList<String>(set);
            result.put(date,newList);
        }
        // 정렬
        return new TreeMap<>(result);
    }
}
