package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Family;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FamilyRepository {
    private final EntityManager em;

    public void save(Family family){
        em.persist(family);
    }

    public Family findOne(int id){
        return em.find(Family.class, id);
    }

    public List<Family> findAll(){
        return em.createQuery("select f from Family f", Family.class)
                .getResultList();
    }
}
