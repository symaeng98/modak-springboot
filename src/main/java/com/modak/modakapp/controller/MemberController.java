package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Anniversary;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.dto.member.MemberAndFamilyMemberDTO;
import com.modak.modakapp.dto.member.MemberDTO;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.service.*;
import com.modak.modakapp.vo.member.InvitationVO;
import com.modak.modakapp.vo.member.info.UpdateMemberFamilyNameVO;
import com.modak.modakapp.vo.member.info.UpdateMemberTagVO;
import com.modak.modakapp.vo.member.info.UpdateMemberVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/member")
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final FamilyService familyService;
    private final AnniversaryService anniversaryService;
    private final TodoService todoService;
    private final TodoDoneService todoDoneService;
    private final LetterService letterService;
    private final TodayTalkService todayTalkService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 초대 받았습니다."),
            @ApiResponse(code = 401, message = "1. 만료된 토큰입니다. (ExpiredJwtException)\n2. 유효하지 않은 토큰입니다. (JwtException)\n3. 헤더에 토큰이 없습니다. (NullPointerException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요."),
    })
    @ApiOperation(value = "초대코드로 해당 가족에 포함하기")
    @PutMapping("/invitations")
    public ResponseEntity<CommonSuccessResponse<MemberAndFamilyMemberDTO>> invite(
            @RequestBody @Valid InvitationVO invitationVO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Family family = familyService.getByCode(invitationVO.getInvitationCode());
        Member member = memberService.getMember(memberId);

        String colorForMember = memberService.getColorForMember(family);
        memberService.updateMemberFamily(member, family, colorForMember);


        MemberAndFamilyMemberDTO memberAndFamilyMemberDTO = MemberAndFamilyMemberDTO.builder()
                .familyCode(family.getCode())
                .memberResult(memberService.getMemberInfo(member))
                .familyMembersResult(memberService.getFamilyMembersInfo(member))
                .build();

        return ResponseEntity.ok(new CommonSuccessResponse<>("회원 초대 받기 성공", memberAndFamilyMemberDTO, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 정보를 수정하였습니다."),
            @ApiResponse(code = 401, message = "1. 만료된 토큰입니다. (ExpiredJwtException)\n2. 유효하지 않은 토큰입니다. (JwtException)\n3. 헤더에 토큰이 없습니다. (NullPointerException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요."),
    })
    @ApiOperation(value = "유저 개인 정보 변경")
    @PutMapping()
    public ResponseEntity<CommonSuccessResponse<MemberDTO>> updateMember(
            @RequestBody @Valid UpdateMemberVO updateMemberVO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member member = memberService.getMember(memberId);

        memberService.updateMember(member, updateMemberVO);

        Anniversary anniversary = anniversaryService.getBirthdayByMember(memberId);
        anniversaryService.updateBirthday(anniversary.getId(), updateMemberVO.getBirthday(), updateMemberVO.getIsLunar());

        MemberDTO memberInfo = memberService.getMemberInfo(member);

        return ResponseEntity.ok(new CommonSuccessResponse<>("회원 개인 정보 변경 성공", memberInfo, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 정보 가져오기를 성공했습니다.."),
            @ApiResponse(code = 401, message = "1. 만료된 토큰입니다. (ExpiredJwtException)\n2. 유효하지 않은 토큰입니다. (JwtException)\n3. 헤더에 토큰이 없습니다. (NullPointerException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요."),
    })
    @ApiOperation(value = "유저 및 가족 정보 얻기")
    @GetMapping()
    public ResponseEntity<CommonSuccessResponse<MemberAndFamilyMemberDTO>> getMember(

    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member member = memberService.getMemberWithFamily(memberId);
        Family family = member.getFamily();

        MemberAndFamilyMemberDTO memberAndFamilyMemberDTO = MemberAndFamilyMemberDTO.builder()
                .familyCode(family.getCode())
                .memberResult(memberService.getMemberInfo(member))
                .familyMembersResult(memberService.getFamilyMembersInfo(member))
                .build();

        return ResponseEntity.ok(new CommonSuccessResponse<>("회원 및 가족 정보 불러오기 성공", memberAndFamilyMemberDTO, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원의 태그 정보 수정에 성공했습니다."),
            @ApiResponse(code = 401, message = "1. 만료된 토큰입니다. (ExpiredJwtException)\n2. 유효하지 않은 토큰입니다. (JwtException)\n3. 헤더에 토큰이 없습니다. (NullPointerException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요."),
    })
    @ApiOperation(value = "유저 개인 태그 업데이트")
    @PutMapping("/tags")
    public ResponseEntity<CommonSuccessResponse<MemberDTO>> updateMemberTag(
            @RequestBody UpdateMemberTagVO updateMemberTagVO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member member = memberService.getMember(memberId);

        memberService.updateMemberTag(member, updateMemberTagVO.getTags());

        MemberDTO memberInfo = memberService.getMemberInfo(member);

        return ResponseEntity.ok(new CommonSuccessResponse<>("회원 개인 태그 업데이트 성공", memberInfo, true));
    }

//    @ApiResponses({
//            @ApiResponse(code = 200, message = "회원의 가족들의 정보 가져오기를 성공했습니다.."),
//            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException)\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
//            @ApiResponse(code = 401, message = "만료된 Access Token 입니다.(ExpiredAccessTokenException)"),
//    })
//    @ApiOperation(value = "가족들 정보 얻기")
//    @GetMapping("/{id}/family")
//    public ResponseEntity<CommonSuccessResponse<MemberAndFamilyMemberDTO>> getFamilyMembers(
//            ,
//            @PathVariable("id") int memberId
//    ) {
//        tokenService.validateAccessTokenExpired(accessToken);
//
//        Member member = memberService.getMemberWithFamily(memberId);
//        Family family = member.getFamily();
//
//        MemberAndFamilyMemberDTO memberAndFamilyMemberDTO = new MemberAndFamilyMemberDTO(family.getCode(), memberService.getMemberInfo(member), memberService.getFamilyMembersInfo(member));
//
//        return ResponseEntity.ok(new CommonSuccessResponse<>("회원 및 가족 정보 불러오기 성공", memberAndFamilyMemberDTO, true));
//    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원의 가족 이름 정보 수정에 성공했습니다."),
            @ApiResponse(code = 401, message = "1. 만료된 토큰입니다. (ExpiredJwtException)\n2. 유효하지 않은 토큰입니다. (JwtException)\n3. 헤더에 토큰이 없습니다. (NullPointerException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요."),
    })
    @ApiOperation(value = "가족의 이름 별명으로 바꾸기")
    @PutMapping("/family/names")
    public ResponseEntity<CommonSuccessResponse<MemberDTO>> updateFamilyMemberName(
            @RequestBody UpdateMemberFamilyNameVO updateMemberFamilyNameVO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member member = memberService.getMember(memberId);

        memberService.updateMemberFamilyName(member, updateMemberFamilyNameVO.getMemberFamilyName());

        MemberDTO memberInfo = memberService.getMemberInfo(member);

        return ResponseEntity.ok(new CommonSuccessResponse<>("회원의 가족 이름 변경 성공", memberInfo, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 탈퇴를 성공적으로 수행했습니다."),
            @ApiResponse(code = 401, message = "1. 만료된 토큰입니다. (ExpiredJwtException)\n2. 유효하지 않은 토큰입니다. (JwtException)\n3. 헤더에 토큰이 없습니다. (NullPointerException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요."),
    })
    @DeleteMapping()
    public ResponseEntity<CommonSuccessResponse<String>> deleteMember(
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member member = memberService.getMember(memberId);

        anniversaryService.deleteAllByMember(member);
        letterService.deleteAllByMember(member);
        todayTalkService.deleteAllByMember(member);
        todoDoneService.deleteAllByMember(member);
        todoService.deleteAllByMember(member);
        memberService.deleteMember(memberId);

        return ResponseEntity.ok(new CommonSuccessResponse<>("회원 탈퇴 성공", null, true));
    }
}