package com.modak.modakapp.service;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.enums.Provider;
import com.modak.modakapp.domain.enums.Role;
import com.modak.modakapp.dto.FamilyMemberDTO;
import com.modak.modakapp.dto.MemberDTO;
import com.modak.modakapp.dto.MemberFamilyNameDTO;
import com.modak.modakapp.dto.metadata.MDFamily;
import com.modak.modakapp.dto.metadata.MDTag;
import com.modak.modakapp.exception.member.NoSuchMemberException;
import com.modak.modakapp.repository.MemberRepository;
import com.modak.modakapp.vo.member.info.UpdateMemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public int join(Member member) {
        memberRepository.save(member);
        return member.getId();
    }

    public Member findMember(int memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다."));
    }

    public Member findMemberByProviderAndProviderId(Provider provider, String providerId) {
        return memberRepository.findByProviderAndProviderId(provider, providerId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다."));
    }

    public Member findMemberWithFamily(int memberId) {
        return memberRepository.findMemberWithFamilyById(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다."));
    }

    public MemberDTO getMemberInfo(int memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다."));

        MemberDTO memberDto = MemberDTO.builder()
                .memberId(member.getId())
                .birthDay(member.getBirthday().toString())
                .color(member.getColor())
                .createdAt(member.getCreatedAt())
                .familyId(member.getFamily().getId())
                .name(member.getName())
                .profileImageUrl(member.getProfileImageUrl())
                .provider(member.getProvider().name())
                .providerId(member.getProviderId())
                .role(member.getRole().name())
                .isLunar(member.getIsLunar())
                .updatedAt(member.getUpdatedAt())
                .build();

        if (member.getMdTag() == null) {
            memberDto.setTags(null);
        } else {
            memberDto.setTags(member.getMdTag().getTags());
        }

        if (member.getMdFamily() == null) {
            memberDto.setFamilyName(null);
        } else {
            memberDto.setFamilyName(member.getMdFamily().getMemberFamilyName());
        }

        return memberDto;
    }

    public List<FamilyMemberDTO> getFamilyMembersInfo(int memberId) {
        Member member = memberRepository.findMemberWithFamilyById(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다."));
        Family family = member.getFamily();

        List<FamilyMemberDTO> result = new ArrayList<>();
        List<Member> members = family.getMembers();

        for (Member m : members) {
            if (m.getId() == memberId) {
                continue;
            }

            FamilyMemberDTO familyMemberDto = FamilyMemberDTO
                    .builder()
                    .birthday(m.getBirthday().toString())
                    .memberId(m.getId())
                    .isLunar(m.getIsLunar())
                    .name(m.getName())
                    .color(m.getColor())
                    .role(m.getRole().name())
                    .profileImageUrl(m.getProfileImageUrl())
                    .build();
            result.add(familyMemberDto);
        }

        return result;
    }

    public List<String> findColors(int familyId) {
        return memberRepository.findColorsByFamilyId(familyId);
    }

    public String getColorForMember(int familyId) {
        String[] colorList = {"FFFFAF3D", "FFE8388A", "FF4955FF", "FF38E8A0", "FFFFFE40", "FFFFEA38",
                "FFE86A33", "FFCF44FF", "FF339EE8", "FF3BFF41", "FFFFD13D", "FFE84C38",
                "FF9149FF", "FF38DBE8", "FF8EFF40"};
        List<String> colors = memberRepository.findColorsByFamilyId(familyId);

        for (String c : colorList) {
            if (!colors.contains(c)) {
                return c;
            }
        }
        return colorList[colors.size() % colorList.length]; // 가족 구성원이 없거나, 모든 색깔을 가족이 다 가지고 있을 때는 계속 반복
    }

    @Transactional
    public void updateMemberTag(int memberId, List<String> tags) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다."));
        member.changeMemberTag(new MDTag(tags));
    }

    @Transactional
    public void updateMemberFamilyName(int memberId, List<MemberFamilyNameDTO> familyName) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다."));
        member.changeMemberFamilyName(new MDFamily(familyName));
    }

    @Transactional
    public void updateRefreshToken(int memberId, String refreshToken) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다."));
        findMember.changeRefreshToken(refreshToken);
    }

    @Transactional
    public void updateMember(int memberId, UpdateMemberVO updateMemberVO) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다."));
        findMember.changeMemberInfo(
                updateMemberVO.getName(),
                Role.valueOf(updateMemberVO.getRole()),
                updateMemberVO.getColor(),
                Date.valueOf(updateMemberVO.getBirthday()),
                updateMemberVO.getIsLunar()
        );
    }

    @Transactional
    public void updateMemberFamily(int memberId, Family family) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다."));
        member.changeFamily(family);
    }

    @Transactional
    public void deleteMember(Member member) {
        member.removeMember(Timestamp.valueOf(LocalDateTime.now()));
    }


    public boolean isMemberExists(String providerId) {
        return memberRepository.isExists(providerId);
    }
}
