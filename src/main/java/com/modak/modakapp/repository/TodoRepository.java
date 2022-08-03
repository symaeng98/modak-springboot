package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.domain.TodoDone;
import com.modak.modakapp.exception.member.NoMemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TodoRepository {
    private final EntityManager em;
    public void save(Todo todo){
        em.persist(todo);
    }

    public Todo findOneByTodoId(int id){
        return em.find(Todo.class,id);
    }


    public List<Todo> findAllByFamilyId(int id){
        try {
            return em.createQuery("select t from Todo t where t.family.id =:id", Todo.class)
                    .setParameter("id",id)
                    .getResultList();
        }catch (NoMemberException e) {
            throw new NoMemberException("등록된 회원 정보가 없습니다.");
        }
    }


}
