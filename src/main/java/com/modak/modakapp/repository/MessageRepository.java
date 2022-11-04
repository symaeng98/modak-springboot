package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRepository {
    private final EntityManager em;

    public List<Message> findMessageByCount(Family family, int count) {
        return em.createQuery(
                        "select m from Message m" +
                                " join fetch m.member" +
                                " where m.family = :family" +
                                " order by m.id DESC ", Message.class
                )
                .setParameter("family", family)
                .setMaxResults(count)
                .getResultList();
    }

    public List<Message> findMessageByCountAndLastId(Family family, int count, int lastId) {
        return em.createQuery(
                        "select m from Message m" +
                                " join fetch m.member" +
                                " where m.family = :family" +
                                " and m.id < :lastId" +
                                " order by m.id DESC ", Message.class
                )
                .setParameter("family", family)
                .setParameter("lastId", lastId)
                .setMaxResults(count)
                .getResultList();
    }
}