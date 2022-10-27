package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Letter;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.dto.letter.LettersDTO;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.service.LetterService;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.vo.letter.LetterVO;
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
import java.sql.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/letter")
@Slf4j
public class LetterController {
    private final MemberService memberService;
    private final LetterService letterService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 편지를 등록했습니다."),
            @ApiResponse(code = 401, message = "1. 만료된 토큰입니다. (ExpiredJwtException)\n2. 유효하지 않은 토큰입니다. (JwtException)\n3. 헤더에 토큰이 없습니다. (NullPointerException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요."),
    })
    @ApiOperation(value = "편지 등록")
    @PostMapping()
    public ResponseEntity<CommonSuccessResponse<LettersDTO>> createLetter(
            @RequestBody @Valid LetterVO letterVO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member fromMember = memberService.getMemberWithFamily(memberId);
        Member toMember = memberService.getMember(letterVO.getToMemberId());

        Letter letter = Letter.builder()
                .fromMember(fromMember)
                .toMember(toMember)
                .content(letterVO.getContent())
                .date(Date.valueOf(letterVO.getDate()))
                .envelope(letterVO.getEnvelope())
                .family(fromMember.getFamily())
                .build();

        letterService.join(letter);

        LettersDTO lettersDto = letterService.getLettersByMember(fromMember);

        return ResponseEntity.ok(new CommonSuccessResponse<>("편지 등록 성공", lettersDto, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 편지 목록을 가져왔습니다."),
            @ApiResponse(code = 401, message = "1. 만료된 토큰입니다. (ExpiredJwtException)\n2. 유효하지 않은 토큰입니다. (JwtException)\n3. 헤더에 토큰이 없습니다. (NullPointerException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @ApiOperation(value = "편지 목록 조회")
    @GetMapping()
    public ResponseEntity<CommonSuccessResponse<LettersDTO>> getSentLetter(
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member member = memberService.getMemberWithFamily(memberId);

        LettersDTO lettersDto = letterService.getLettersByMember(member);

        return ResponseEntity.ok(new CommonSuccessResponse<>("회원의 편지 목록 불러오기 성공", lettersDto, true));
    }
}
