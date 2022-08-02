package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Member;
import com.modak.modakapp.exception.member.NoMemberException;
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

    public Member findOne(int id){
        return em.find(Member.class, id);
    }

    public Member findOneByProviderId(String providerId){
        try {
            return em.createQuery("select m from Member m where m.providerId =:providerId", Member.class)
                    .setParameter("providerId",providerId)
                    .getSingleResult();
        }catch (NoMemberException e) {
            throw new NoMemberException("등록된 회원 정보가 없습니다.");
        }
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }



}
