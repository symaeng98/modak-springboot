package com.modak.modakapp.service;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.metadata.MDFamily;
import com.modak.modakapp.domain.metadata.MDTag;
import com.modak.modakapp.dto.MemberDataDTO;
import com.modak.modakapp.vo.member.UpdateMemberVO;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.enums.Role;
import com.modak.modakapp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public MemberDataDTO getMemberInfo(int memberId){
        Member member = findMember(memberId);

        return MemberDataDTO.builder().id(member.getId()).birthDay(member.getBirthday().toString())
                .color(member.getColor()).createdAt(member.getCreatedAt()).familyId(member.getFamily().getId())
                .tags(member.getMdTag()).familyName(member.getMdFamily())
                .name(member.getName()).profileImageUrl(member.getProfileImageUrl())
                .provider(member.getProvider().name()).providerId(member.getProviderId())
                .role(member.getRole().name()).isLunar(member.getIs_lunar()).updatedAt(member.getUpdatedAt())
                .build();
    }


    public void updateRefreshToken(int memberId, String refreshToken){
        Member findMember = memberRepository.findOne(memberId);
        isDeleted(findMember);
        findMember.setRefreshToken(refreshToken);
    }

    public void updateMember(int memberId, UpdateMemberVO updateMemberVO){
        Member findMember = memberRepository.findOne(memberId);
        findMember.setName(updateMemberVO.getName());
        findMember.setRole(Role.valueOf(updateMemberVO.getRole()));
        findMember.setColor(updateMemberVO.getColor());
        findMember.setBirthday(Date.valueOf(updateMemberVO.getBirthday()));
        findMember.setIs_lunar(updateMemberVO.getIsLunar());
    }

    public void updateMemberFamily(int memberId, Family family){
        Member member = memberRepository.findOne(memberId);
        member.setFamily(family);
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
