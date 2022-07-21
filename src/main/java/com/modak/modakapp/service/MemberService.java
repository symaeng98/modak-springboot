package com.modak.modakapp.service;

import com.modak.modakapp.domain.Member;
import com.modak.modakapp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Long join(Member member){
        memberRepository.save(member);
        return member.getId();
    }

    

}
