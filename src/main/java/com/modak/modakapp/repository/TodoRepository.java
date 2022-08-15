package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.exception.member.NoSuchMemberException;
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


    public List<Todo> findAllByGroupId(int id){
        try {
            return em.createQuery("select t from Todo t where t.groupTodoId =:id and t.deletedAt is null ", Todo.class)
                    .setParameter("id",id)
                    .getResultList();
        }catch (NoSuchMemberException e) {
            throw new NoSuchMemberException("등록된 Todo 정보가 없습니다.");
        }
    }



    public List<Todo> findAllByFamilyId(int id){
        try {
            return em.createQuery("select t from Todo t where t.family.id =:id and t.deletedAt is null ", Todo.class)
                    .setParameter("id",id)
                    .getResultList();
        }catch (NoSuchMemberException e) {
            throw new NoSuchMemberException("등록된 Todo 정보가 없습니다.");
        }
    }




}
