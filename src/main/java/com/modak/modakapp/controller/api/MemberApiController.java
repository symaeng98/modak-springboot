package com.modak.modakapp.controller.api;

import com.modak.modakapp.DTO.CommonFailResponse;
import com.modak.modakapp.DTO.CommonSuccessResponse;
import com.modak.modakapp.DTO.Member.CreateMemberResponse;
import com.modak.modakapp.DTO.Member.LoginMemberResponse;
import com.modak.modakapp.DTO.Token.ReissueTokenResponse;
import com.modak.modakapp.VO.Member.LoginMemberVO;
import com.modak.modakapp.VO.Member.OpenVO;
import com.modak.modakapp.VO.Member.SignUpMemberVO;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Provider;
import com.modak.modakapp.domain.Role;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.exception.MemberAlreadyExistsException;
import com.modak.modakapp.service.FamilyService;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.Jwt.TokenService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Slf4j
public class MemberApiController {

    private final MemberService memberService;
    private final FamilyService familyService;

    private final TokenService tokenService;

    private final HttpServletResponse servletResponse;

    private final String TOKEN_HEADER = "Bearer ";

    @ApiOperation(value = "회원 가입")
    @PostMapping("/new")
    public ResponseEntity create(@RequestBody @ApiParam(value = "회원 기본 정보",required = true) SignUpMemberVO signUpMemberVO){
        try {
            if(memberService.isMemberExists(signUpMemberVO.getProviderId())){
                throw new MemberAlreadyExistsException();
            }
            // 생년월일 포맷 확인
            Date birthday = new SimpleDateFormat("yyyyMMdd").parse(signUpMemberVO.getBirthday().replace("-",""));
            java.sql.Date birthdaySqlDate = new java.sql.Date(birthday.getTime());

            Family family = Family.builder().name("행복한 우리 가족").build();
            int joinFamilyId = familyService.join(family);

            Member member = Member.builder().family(family).name(signUpMemberVO.getName()).is_lunar(signUpMemberVO.getIsLunar())
                    .birthday(birthdaySqlDate).role(Role.valueOf(signUpMemberVO.getRole()))
                    .provider(Provider.valueOf(signUpMemberVO.getProvider())).providerId(signUpMemberVO.getProviderId())
                    .chatLastJoined(Timestamp.valueOf(LocalDateTime.now())).chatNowJoining(0)
                    .refreshToken("default refresh").fcmToken("default fcm").build();

            // 저장
            int memberId = memberService.join(member);

            String accessToken = tokenService.getAccessToken(memberId);
            String refreshToken = tokenService.getRefreshToken(memberId);
            memberService.updateRefreshToken(memberId, refreshToken);

            servletResponse.setHeader("ACCESS_TOKEN", TOKEN_HEADER + accessToken);
            servletResponse.setHeader("REFRESH_TOKEN", TOKEN_HEADER + refreshToken);

            return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response("회원 생성 완료", new CreateMemberResponse(memberId, joinFamilyId)));

        }catch (ParseException e){
            e.printStackTrace();
            return ResponseEntity.ok(CommonFailResponse.response("생년월일 포맷이 yyyy-MM-dd인지 확인하세요"));
        }catch (MemberAlreadyExistsException e){
            e.printStackTrace();
            return ResponseEntity.ok(CommonFailResponse.response("이미 가입된 회원입니다."));
        }
    }


    // 이름 변경
    @PostMapping("/social-login")
    public ResponseEntity<?> login(@RequestBody @ApiParam(value = "Provider, ProviderId",required = true) LoginMemberVO loginMemberVO){
        String providerId = loginMemberVO.getProviderId();
        try{
            Member findMember = memberService.findMemberByProviderId(providerId);
            int memberId = findMember.getId();
            String newRefreshToken = tokenService.getRefreshToken(memberId);
            String newAccessToken = tokenService.getAccessToken(memberId);
            memberService.updateRefreshToken(memberId,newRefreshToken);
            servletResponse.setHeader("ACCESS_TOKEN", TOKEN_HEADER + newAccessToken);
            servletResponse.setHeader("REFRESH_TOKEN", TOKEN_HEADER + newRefreshToken);

            return ResponseEntity.ok(CommonSuccessResponse.response("로그인 성공", new LoginMemberResponse(memberId)));
        }catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            return ResponseEntity.ok(CommonFailResponse.response("회원 정보가 없습니다. 회원가입 페이지로 이동하세요"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.ok(CommonFailResponse.response("로그인 실패"));
    }


    // 토큰 로그인 로직 바꾸기
    /**
     *
     * @param openVO accessToken, refreshToken
     * @return accessToken
     * accessToken이 만료되었다는 가정 하에 refreshToken이 만료되지 않았다면 accessToken 발급
     */
    @ApiResponses({
            @ApiResponse(code=200, message = "서버는 잘 동작했음 status false면 넘겨주는 값 확인"),
            @ApiResponse(code=401, message = "Refresh 토큰 만료됨, 재로그인"),
            })
    @PostMapping("/token-login")
    public ResponseEntity reissue(@RequestBody @ApiParam(value = "가지고 있는 Access 토큰과 Refresh 토큰",required = true) OpenVO openVO){
        String accessToken = openVO.getAccessToken().substring(7);
        String refreshToken = openVO.getRefreshToken().substring(7);
        // bearer
        try {
            int memberId = tokenService.getMemberId(accessToken);
            int memberId1 = tokenService.getMemberId(refreshToken);

            String newAccessToken = tokenService.getAccessToken(memberId);
            String newRefreshToken = tokenService.getRefreshToken(memberId);
            memberService.updateRefreshToken(memberId,newRefreshToken);
            servletResponse.setHeader("ACCESS_TOKEN", TOKEN_HEADER + newAccessToken);
            servletResponse.setHeader("REFRESH_TOKEN", TOKEN_HEADER + newRefreshToken);
            return ResponseEntity.ok(CommonSuccessResponse.response("토큰 재발급 성공", new ReissueTokenResponse("ACCESS_AND_REFRESH_TOKEN")));
        } catch (Exception e) {
            // 에러 처리 로직
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response(e.getMessage()));
        }
    }

}