package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.dto.message.ConnectionResult;
import com.modak.modakapp.dto.message.MessageResult;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.service.MessageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/message")
@Slf4j
public class MessageController {
    private final MemberService memberService;
    private final MessageService messageService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 채팅 목록을 불러왔습니다."),
            @ApiResponse(code = 401, message = "1. 만료된 토큰입니다. (ExpiredJwtException)\n2. 유효하지 않은 토큰입니다. (JwtException)\n3. 헤더에 토큰이 없습니다. (NullPointerException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요."),
    })
    @ApiOperation(value = "채팅 목록 불러오기")
    @GetMapping("/chats")
    public ResponseEntity<CommonSuccessResponse<MessageResult>> getMessages(
            @RequestParam int count,
            @RequestParam int lastId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member memberWithFamily = memberService.getMemberWithFamily(memberId);
        Family family = memberWithFamily.getFamily();

        MessageResult messageResult = messageService.getMessagesByFamily(family, count, lastId);

        return ResponseEntity.ok(new CommonSuccessResponse<>("채팅 목록 불러오기 성공", messageResult, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 연결 정보를 불러왔습니다."),
            @ApiResponse(code = 401, message = "1. 만료된 토큰입니다. (ExpiredJwtException)\n2. 유효하지 않은 토큰입니다. (JwtException)\n3. 헤더에 토큰이 없습니다. (NullPointerException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요."),
    })
    @ApiOperation(value = "연결 정보 불러오기")
    @GetMapping("/connections")
    public ResponseEntity<CommonSuccessResponse<ConnectionResult>> geConnectionInfo(

    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member memberWithFamily = memberService.getMemberWithFamily(memberId);
        Family family = memberWithFamily.getFamily();

        List<Member> members = memberService.getMembersByFamily(family);
        ConnectionResult connectionResult = messageService.getConnectionInfoByFamilyMembers(members);

        return ResponseEntity.ok(new CommonSuccessResponse<>("연결 정보 불러오기 성공", connectionResult, true));
    }
}
