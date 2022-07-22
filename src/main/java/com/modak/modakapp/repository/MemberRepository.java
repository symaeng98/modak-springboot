package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member){
        em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    public Member findOneByProviderId(String providerId){
        return em.createQuery("select m from Member m where m.providerId =:providerId", Member.class)
                .setParameter("providerId",providerId)
                .getSingleResult();
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public void setRefreshToken(Member member, String refreshToken){
        member.setRefreshToken(refreshToken);
    }



}
