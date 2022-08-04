package com.modak.modakapp.service;

import com.modak.modakapp.domain.Member;
import com.modak.modakapp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public int join(Member member){
        memberRepository.save(member);
        return member.getId();
    }

    public Member findMember(int memberId){
        Member member = memberRepository.findOne(memberId);
        isDeleted(member);
        return member;
    }

    public Member findMemberByProviderId(String providerId){
        Member member = memberRepository.findOneByProviderId(providerId);
        isDeleted(member);
        return member;
    }


    public void updateRefreshToken(int memberId, String refreshToken){
        Member findMember = memberRepository.findOne(memberId);
        isDeleted(findMember);
        findMember.setRefreshToken(refreshToken);
    }


    public void deleteMember(Member member){
        member.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
    }

    public void isDeleted(Member member){
        if(member.getDeletedAt()!=null){
            throw new NoResultException();
        }
    }

    public boolean isMemberExists(String providerId) {
        try {
            Member findMember = findMemberByProviderId(providerId);
            isDeleted(findMember);
        }catch (EmptyResultDataAccessException e){
            return false;
        }
        return true;
    }
}
