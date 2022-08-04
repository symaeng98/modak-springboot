package com.modak.modakapp.service;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.domain.TodoDone;
import com.modak.modakapp.repository.TodoDoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TodoDoneService {
    private final TodoDoneRepository todoDoneRepository;

    public int join(TodoDone todoDone){
        todoDoneRepository.save(todoDone);
        return todoDone.getId();
    }

    public TodoDone findTodo(int id){
        TodoDone todoDone = todoDoneRepository.findOne(id);
        isDeleted(todoDone);
        return todoDone;
    }


    public int updateIsDone(Todo todo, Date date, int isDone){
        List<TodoDone> todoDones = todo.getTodoDone();
        if(todoDones.size()==0){ // 완료된 적 없음
            Member member = todo.getMember();
            Family family = todo.getFamily();
            TodoDone todoDone = TodoDone.builder().member(member).family(family)
                    .todo(todo).isDone(isDone)
                    .date(date).build();
            todoDoneRepository.save(todoDone);
            return todoDone.getId();
        }
        List<TodoDone> todoDoneList = todoDones.stream().filter(t -> t.getDate().equals(date)).collect(Collectors.toList());
        if(todoDoneList.size()==0){ // 다른 단일은 있는데, 같은 날짜 찾아보니 없음
            Member member = todo.getMember();
            Family family = todo.getFamily();
            TodoDone todoDone = TodoDone.builder().member(member).family(family)
                    .todo(todo).isDone(isDone)
                    .date(date).build();
            todoDoneRepository.save(todoDone);
            return todoDone.getId();
        }
        TodoDone todoDone = todoDoneList.get(0);
        todoDone.setIsDone(isDone);
        return todoDone.getId();
    }

    public void isDeleted(TodoDone todoDone){
        if(todoDone.getDeletedAt()!=null){
            throw new NoResultException();
        }
    }
}
