package com.modak.modakapp.service;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.domain.TodoDone;
import com.modak.modakapp.repository.TodoDoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TodoDoneService {
    private final TodoDoneRepository todoDoneRepository;

    @Transactional
    public int join(TodoDone todoDone) {
        todoDoneRepository.save(todoDone);
        return todoDone.getId();
    }

//    public TodoDone findTodo(int id) {
//        TodoDone todoDone = todoDoneRepository.findOne(id);
//        isDeleted(todoDone);
//        return todoDone;
//    }

    public int getNumOfTodoDone(Family family) {
        Long doneNum = todoDoneRepository.findNumOfDoneByFamilyId(family);
        return doneNum.intValue();
    }

    @Transactional
    public void updateIsDone(Todo todo, Date date, int isDone) {
//        List<TodoDone> todoDones = todoDoneRepository.findAllByTodo(todo);
        List<TodoDone> todoDones = todo.getTodoDoneList();
        System.out.println(todoDones.size());
        if (todoDones.size() == 0) { // 완료된 적 없음
            Member member = todo.getMember();
            Family family = todo.getFamily();
            TodoDone todoDone = TodoDone.builder()
                    .member(member)
                    .family(family)
                    .isDone(isDone)
                    .date(date)
                    .build();
            todoDone.changeTodo(todo);
            todoDoneRepository.save(todoDone);
            return;
        }
        List<TodoDone> todoDoneList = todoDones.stream().filter(t -> t.getDate().equals(date)).collect(Collectors.toList());
        if (todoDoneList.size() == 0) { // 다른 단일은 있는데, 같은 날짜 찾아보니 없음
            Member member = todo.getMember();
            Family family = todo.getFamily();
            TodoDone todoDone = TodoDone.builder()
                    .member(member)
                    .family(family)
                    .isDone(isDone)
                    .date(date)
                    .build();
            todoDone.changeTodo(todo);
            todoDoneRepository.save(todoDone);
            return;
        }
        TodoDone todoDone = todoDoneList.get(0);
        todoDone.changeIsDone(isDone);
    }

    @Transactional
    public void deleteTodoDone(Todo todo, String strDate) {
        Date date = Date.valueOf(strDate);
//        List<TodoDone> todoDoneList = todoDoneRepository.findAllByTodo(todo);
        List<TodoDone> todoDoneList = todo.getTodoDoneList();
        for (TodoDone td : todoDoneList) {
            if (td.getDate().before(date)) continue;
            td.removeTodoDone(Timestamp.valueOf(LocalDateTime.now()));
        }
    }

    @Transactional
    public void deleteAllByMember(Member member) {
        todoDoneRepository.deleteAllByMember(member, Timestamp.valueOf(LocalDateTime.now()));
    }
}
