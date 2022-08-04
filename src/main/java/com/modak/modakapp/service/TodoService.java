package com.modak.modakapp.service;

import com.modak.modakapp.VO.Todo.UpdateTodoVO;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.domain.TodoDone;
import com.modak.modakapp.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public int join(Todo todo){
        todoRepository.save(todo);
        return todo.getId();
    }

    public Todo findTodo(int id){
        Todo todo = todoRepository.findOneByTodoId(id);
        isDeleted(todo);
        return todo;
    }


    public void deleteTodo(Todo todo){
        todo.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
    }

    public void isDeleted(Todo todo){
        if(todo.getDeletedAt()!=null){
            throw new NoResultException();
        }
    }

    public void updateGroupTodoId(int id, int groupTodoId){
        Todo findTodo = todoRepository.findOneByTodoId(id);
        findTodo.setGroupTodoId(groupTodoId);
    }

    public void updateTodo(int todoId, String title, String memo, Member member, String date, String timeTag){
        Todo todo = todoRepository.findOneByTodoId(todoId);
        isDeleted(todo);
        todo.setTitle(title);
        todo.setMemo(memo);
        todo.setMember(member);
        todo.setStartDate(Date.valueOf(date));
        todo.setTimeTag(timeTag);

//        String repeatTag = getRepeatTag(repeat);
//        // 반복 x
//        if(repeatTag==null){
//            todo.setEndDate(java.sql.Date.valueOf(date));
//        }
//
//        // 반복
//        todo.setEndDate(java.sql.Date.valueOf("2025-01-01"));
//        todo.setRepeatTag(repeatTag);
//        todo.setIsSunday(repeat.get(0));
//        todo.setIsMonday(repeat.get(1));
//        todo.setIsTuesday(repeat.get(2));
//        todo.setIsWednesday(repeat.get(3));
//        todo.setIsThursday(repeat.get(4));
//        todo.setIsFriday(repeat.get(5));
//        todo.setIsSaturday(repeat.get(6));
    }


    public void updateEndDate(int todoId, Date date){
        Todo findTodo = todoRepository.findOneByTodoId(todoId);
        findTodo.setEndDate(date);
    }



    public int getGauge(Family family){
        return todoRepository.findNumOfDone(family);
    }



    public String getRepeatTag(List<Integer> repeat){
        String [] day = {"일","월","화","수","목","금","토"};

        String repeatTag = "";
        if(repeat.get(1)==0&&repeat.get(2)==0&&repeat.get(3)==0&&repeat.get(4)==0&&repeat.get(5)==0&&repeat.get(0)==0&&repeat.get(6)==0){
            return null;
        }

        // 주중
        if(repeat.get(1)==1&&repeat.get(2)==1&&repeat.get(3)==1&&repeat.get(4)==1&&repeat.get(5)==1){
            repeatTag = "주중";
            if(repeat.get(0)==1&&repeat.get(6)==1){
                repeatTag = "매일";
            }
            else if(repeat.get(0)==1){
                repeatTag += ", 일";
            }
            else if(repeat.get(6)==1){
                repeatTag += ", 토";
            }
            else {
                return repeatTag;
            }
        }
        else if(repeat.get(1)==0&&repeat.get(2)==0&&repeat.get(3)==0&&repeat.get(4)==0&&repeat.get(5)==0&&repeat.get(0)==1&&repeat.get(6)==1){
            repeatTag = "주말";
        }
        else{
            int i = 0;
            for (Integer r : repeat) {
                if(r==1){
                    repeatTag = repeatTag + day[i] + ", ";
                }
                i++;
            }
            return repeatTag.substring(0, repeatTag.length() - 2);
        }
        return repeatTag;
    }
}
