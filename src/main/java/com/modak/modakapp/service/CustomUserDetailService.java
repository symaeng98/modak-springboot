package com.modak.modakapp.service;

import com.modak.modakapp.exception.member.NoSuchMemberException;
import com.modak.modakapp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) {
        return memberRepository.findById(Integer.parseInt(memberId)).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다."));
    }
}
