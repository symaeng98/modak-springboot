package com.modak.modakapp.service;

import com.modak.modakapp.domain.Member;
import com.modak.modakapp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final FamilyService familyService;

    public Long join(Member member){
        memberRepository.save(member);
        return member.getId();
    }

    public Member findMember(Long memberId){
        return memberRepository.findOne(memberId);
    }

    public Member findMemberByProviderId(String providerId){
        return memberRepository.findOneByProviderId(providerId);
    }

    @Transactional
    public void updateRefreshToken(Long memberId, String refreshToken){
        Member findMember = memberRepository.findOne(memberId);
        findMember.setRefreshToken(refreshToken);
    }

}
