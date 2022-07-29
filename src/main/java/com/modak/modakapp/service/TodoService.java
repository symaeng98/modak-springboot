package com.modak.modakapp.service;

import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return todoRepository.findOneByTodoId(id);
    }
}
