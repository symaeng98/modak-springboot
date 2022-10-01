package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Anniversary;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.enums.Category;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.dto.response.anniversary.AnniversaryResponse;
import com.modak.modakapp.dto.response.anniversary.DateAnniversaryResponse;
import com.modak.modakapp.service.AnniversaryService;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.utils.jwt.TokenService;
import com.modak.modakapp.vo.anniversary.AnniversaryVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/anniversary")
@Slf4j
public class AnniversaryController {
    private final TokenService tokenService;
    private final AnniversaryService anniversaryService;
    private final MemberService memberService;
    private final String ACCESS_TOKEN = "Access-Token";

    // 공통되는 응답 코멘트 부분 변수로 묶기 - 추후 (ACCESS_TOKEN_EXCEPTION_MESSAGE)
    @ApiResponses({
            @ApiResponse(code = 201, message = "기념일 생성을 성공하였습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "1. Access Token이 만료되었습니다.(ExpiredAccessTokenException)\n2. 만료된 Refresh Token 입니다. 다시 로그인하세요.(ExpiredRefreshTokenException)"),
    })
    @ApiOperation(value = "기념일 생성")
    @PostMapping()
    public ResponseEntity<CommonSuccessResponse<AnniversaryResponse>> createAnniversary(
            @ApiParam(value = "기념일 생성 정보 및 fromDate, toDate", required = true)
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @RequestBody AnniversaryVO anniversaryVO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member memberWithFamily = memberService.getMemberWithFamily(memberId);

        Family family = memberWithFamily.getFamily();
        int familyId = family.getId();


        // 날짜 변형
        Date date = Date.valueOf(anniversaryVO.getDate());

        Anniversary anniversary = Anniversary.builder()
                .member(memberWithFamily)
                .family(family)
                .title(anniversaryVO.getTitle())
                .memo(anniversaryVO.getMemo())
                .category(Category.valueOf(anniversaryVO.getCategory()))
                .isYear(anniversaryVO.getIsYear())
                .isLunar(anniversaryVO.getIsLunar())
                .startDate(date)
                .endDate(date)
                .build();

        int anniversaryId = anniversaryService.join(anniversary);

        DateAnniversaryResponse dar = anniversaryService.getAnniversariesByDate(anniversaryVO.getFromDate(), anniversaryVO.getToDate(), family);

        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonSuccessResponse<>("기념일 생성 완료", new AnniversaryResponse(familyId, anniversaryId, dar), true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "기념일 정보 변경에 성공하였습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "1. Access Token이 만료되었습니다.(ExpiredAccessTokenException)\n2. 만료된 Refresh Token 입니다. 다시 로그인하세요.(ExpiredRefreshTokenException)"),
    })
    @ApiOperation(value = "기념일 정보 변경")
    @PutMapping("/{anniversary_id}")
    public ResponseEntity<CommonSuccessResponse<AnniversaryResponse>> updateAnniversary(
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @PathVariable("anniversary_id") int annId,
            @RequestBody AnniversaryVO anniversaryVO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member memberWithFamily = memberService.getMemberWithFamily(memberId);

        Family family = memberWithFamily.getFamily();
        int familyId = family.getId();

        anniversaryService.updateAnniversary(annId, anniversaryVO);

        DateAnniversaryResponse dar = anniversaryService.getAnniversariesByDate(anniversaryVO.getFromDate(), anniversaryVO.getToDate(), family);

        return ResponseEntity.ok(new CommonSuccessResponse<>("기념일 수정 완료", new AnniversaryResponse(familyId, annId, dar), true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "기념일 삭제에 성공하였습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "1. Access Token이 만료되었습니다.(ExpiredAccessTokenException)\n2. 만료된 Refresh Token 입니다. 다시 로그인하세요.(ExpiredRefreshTokenException)"),
    })
    @ApiOperation(value = "기념일 삭제")
    @DeleteMapping("/{anniversary_id}")
    public ResponseEntity<CommonSuccessResponse<AnniversaryResponse>> deleteAnniversary(
            @ApiParam(value = "기념일 삭제 id 및 fromDate, toDate", required = true)
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @PathVariable("anniversary_id") int annId,
            @RequestParam String fromDate,
            @RequestParam String toDate
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member memberWithFamily = memberService.getMemberWithFamily(memberId);

        Family family = memberWithFamily.getFamily();
        int familyId = family.getId();

        anniversaryService.deleteAnniversary(annId);

        DateAnniversaryResponse dar = anniversaryService.getAnniversariesByDate(fromDate, toDate, family);

        return ResponseEntity.ok(new CommonSuccessResponse<>("기념일 삭제 완료", new AnniversaryResponse(familyId, annId, dar), true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "기념일 불러오기에 성공하였습니다."),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
            @ApiResponse(code = 401, message = "1. Access Token이 만료되었습니다.(ExpiredAccessTokenException)\n2. 만료된 Refresh Token 입니다. 다시 로그인하세요.(ExpiredRefreshTokenException)"),
    })
    @ApiOperation(value = "날짜 범위로 기념일 불러오기")
    @GetMapping()
    public ResponseEntity<CommonSuccessResponse<DateAnniversaryResponse>> getAnniversaries(
            @ApiParam(value = "fromDate~toDate 기념일 정보", required = true)
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @RequestParam String fromDate,
            @RequestParam String toDate
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int memberId = Integer.parseInt(authentication.getName());

        Member memberWithFamily = memberService.getMemberWithFamily(memberId);

        Family family = memberWithFamily.getFamily();

        DateAnniversaryResponse dar = anniversaryService.getAnniversariesByDate(fromDate, toDate, family);

        return ResponseEntity.ok(new CommonSuccessResponse<>("해당 날짜의 기념일을 불러왔습니다.", dar, true));
    }
}
