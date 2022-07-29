package com.modak.modakapp.service;

import com.modak.modakapp.domain.Member;
import com.modak.modakapp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final FamilyService familyService;

    public int join(Member member){
        memberRepository.save(member);
        return member.getId();
    }

    public Member findMember(int memberId){
        return memberRepository.findOne(memberId);
    }

    public Member findMemberByProviderId(String providerId){
        return memberRepository.findOneByProviderId(providerId);
    }

    @Transactional
    public void updateRefreshToken(int memberId, String refreshToken){
        Member findMember = memberRepository.findOne(memberId);
        findMember.setRefreshToken(refreshToken);
    }


    public boolean isMemberExists(String providerId) {
        try {
            Member findMember = findMemberByProviderId(providerId);
        }catch (EmptyResultDataAccessException e){
            return false;
        }
        return true;
    }
}
