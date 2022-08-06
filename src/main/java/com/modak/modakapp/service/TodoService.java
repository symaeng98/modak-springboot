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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public int join(Todo todo) {
        todoRepository.save(todo);
        return todo.getId();
    }

    public Todo findTodo(int id) {
        Todo todo = todoRepository.findOneByTodoId(id);
        isDeleted(todo);
        return todo;
    }



    public void isDeleted(Todo todo) {
        if (todo.getDeletedAt() != null) {
            throw new NoResultException();
        }
    }

    public void updateGroupTodoId(int id, int groupTodoId) {
        Todo findTodo = todoRepository.findOneByTodoId(id);
        isDeleted(findTodo);
        findTodo.setGroupTodoId(groupTodoId);
    }

    public void updateTodo(int todoId, String title, String memo, Member member, String date, String timeTag) {
        Todo todo = todoRepository.findOneByTodoId(todoId);
        isDeleted(todo);
        todo.setTitle(title);
        todo.setMemo(memo);
        todo.setMember(member);
        todo.setStartDate(Date.valueOf(date));
        todo.setEndDate(Date.valueOf(date));
        todo.setTimeTag(timeTag);

    }

    public Date updateEndDateYest(int todoId, Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        Date newDate = new Date(cal.getTime().getTime());

        Todo findTodo = todoRepository.findOneByTodoId(todoId);
        findTodo.setEndDate(newDate);
        return newDate;
    }

    public Date updateStartDateTom(int todoId, Date date){
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        Date newDate = new Date(cal.getTime().getTime());

        Todo findTodo = todoRepository.findOneByTodoId(todoId);
        findTodo.setStartDate(newDate);
        return newDate;
    }


    public void updateEndDate(int todoId, Date date) {
        Todo findTodo = todoRepository.findOneByTodoId(todoId);
        isDeleted(findTodo);
        findTodo.setEndDate(date);
    }

    public void deleteTodo(int todoId) {
        List<Todo> todos = todoRepository.findAllByGroupId(todoId);
        todos.forEach(t -> {
            t.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
            List<TodoDone> todoDone = t.getTodoDone();
            todoDone.forEach(td -> td.setDeletedAt(Timestamp.valueOf(LocalDateTime.now())));
        });
    }

    public void deleteSingleTodo(int todoId){
        Todo findTodo = todoRepository.findOneByTodoId(todoId);
        List<TodoDone> todoDone = findTodo.getTodoDone();
        todoDone.forEach(t->t.setDeletedAt(Timestamp.valueOf(LocalDateTime.now())));
        findTodo.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
    }

    public void deleteByGroupTodoId(int groupTodoId, Date date){
        List<Todo> todoList = todoRepository.findAllByGroupId(groupTodoId);
        if(todoList.size()==1){ // 반복 투두가 하나면
            Todo todo = todoList.get(0);
            updateEndDateYest(todo.getId(),date);
            // 고민: row 하나 만들어서 delete 해야되나?
        }
        else{
            System.out.println(todoList.size());
            todoList.forEach(t->{
                // date보다 나중에 있는 일정들이면 삭제
                if(t.getStartDate().after(date)){
                    t.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
                    List<TodoDone> todoDones = t.getTodoDone();
                    todoDones.forEach(td->{
                        if(td.getDate().after(date)){
                            td.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
                        }
                    });
                }
            });
        }
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
}
