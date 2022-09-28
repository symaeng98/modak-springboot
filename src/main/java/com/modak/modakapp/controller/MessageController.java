package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.dto.message.ConnectionResult;
import com.modak.modakapp.dto.message.MessageResult;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.service.MessageService;
import com.modak.modakapp.utils.jwt.TokenService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
@Slf4j
public class MessageController {
    private final MemberService memberService;
    private final TokenService tokenService;
    private final MessageService messageService;
    private final String ACCESS_TOKEN = "Access-Token";

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 채팅 목록을 불러왔습니다."),
            @ApiResponse(code = 404, message = "회원 정보가 없습니다. (NoSuchMemberException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @ApiOperation(value = "채팅 목록 불러오기")
    @GetMapping("/chat")
    public ResponseEntity<CommonSuccessResponse<MessageResult>> getMessages(
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @RequestParam int count,
            @RequestParam int lastId
    ) {
        tokenService.validateAccessTokenExpired(accessToken);

        int memberId = tokenService.getMemberId(accessToken.substring(7));

        Member memberWithFamily = memberService.getMemberWithFamily(memberId);
        Family family = memberWithFamily.getFamily();

        MessageResult messageResult = messageService.getMessagesByFamily(family, count, lastId);

        return ResponseEntity.ok(new CommonSuccessResponse<>("채팅 목록 불러오기 성공", messageResult, true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 연결 정보를 불러왔습니다."),
            @ApiResponse(code = 404, message = "회원 정보가 없습니다. (NoSuchMemberException)"),
            @ApiResponse(code = 400, message = "에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @ApiOperation(value = "연결 정보 불러오기")
    @GetMapping("/connection")
    public ResponseEntity<CommonSuccessResponse<ConnectionResult>> geConnectionInfo(
            @RequestHeader(value = ACCESS_TOKEN) String accessToken
    ) {
        tokenService.validateAccessTokenExpired(accessToken);

        int memberId = tokenService.getMemberId(accessToken.substring(7));

        Member memberWithFamily = memberService.getMemberWithFamily(memberId);
        Family family = memberWithFamily.getFamily();

        List<Member> members = memberService.getMembersByFamily(family);
        ConnectionResult connectionResult = messageService.getConnectionInfoByFamilyMembers(members);

        return ResponseEntity.ok(new CommonSuccessResponse<>("연결 정보 불러오기 성공", connectionResult, true));
    }
}
