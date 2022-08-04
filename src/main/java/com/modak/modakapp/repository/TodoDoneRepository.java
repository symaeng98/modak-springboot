package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.domain.TodoDone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.sql.Date;

@Repository
@RequiredArgsConstructor
public class TodoDoneRepository {
    private final EntityManager em;

    public void save(TodoDone todoDone){
        em.persist(todoDone);
    }

    public TodoDone findOne(int id){
        return em.find(TodoDone.class, id);
    }

}
