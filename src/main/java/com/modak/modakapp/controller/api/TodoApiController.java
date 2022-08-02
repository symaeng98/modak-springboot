package com.modak.modakapp.controller.api;

import com.modak.modakapp.DTO.CommonFailResponse;
import com.modak.modakapp.DTO.CommonSuccessResponse;
import com.modak.modakapp.DTO.Todo.CreateTodoResponse;
import com.modak.modakapp.Jwt.TokenService;
import com.modak.modakapp.VO.Todo.CreateTodoVO;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.exception.token.ExpiredAccessTokenException;
import com.modak.modakapp.service.FamilyService;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.service.TodoService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo")
@Slf4j
public class TodoApiController {
    private final TodoService todoService;
    private final MemberService memberService;

    private final TokenService tokenService;

    private final HttpServletResponse servletResponse;

    @ApiResponses({
            @ApiResponse(code = 201, message = "할 일 등록에 성공하였습니다."),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. 생년월일 포멧이 잘못되었습니다. yyyy-MM-dd인지 확인하세요. (ParseException)\n2. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n3. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n4. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @PostMapping("/new")
    public ResponseEntity create(@RequestBody CreateTodoVO createTodoVO) {
        String accessToken = createTodoVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);


        // 멤버 가져오기
        int memberId = tokenService.getMemberId(accessToken);
        Member findMember = memberService.findMember(memberId);

        // 가족 가져오기
        Family family = findMember.getFamily();
        int familyId = family.getId();


        // 날짜 변형

        java.sql.Date startDate = java.sql.Date.valueOf(createTodoVO.getDate());
        java.sql.Date endDate = java.sql.Date.valueOf("2023-01-01");



        // 반복 로직
        List<Integer> repeat = createTodoVO.getRepeat();

        String repeatTag = todoService.getRepeatTag(repeat);
        // 반복 x
        if(repeatTag==null){
            endDate = startDate;
        }
        System.out.println(repeatTag);

        Todo todo = Todo.builder().member(findMember).
                family(family).
                title(createTodoVO.getTitle()).
                memo(createTodoVO.getMemo()).
                timeTag(createTodoVO.getTimeTag()).
                date(startDate).
                startDate(startDate).
                endDate(endDate).
                repeatTag(repeatTag).
                isSunday(repeat.get(0)).
                isMonday(repeat.get(1)).
                isTuesday(repeat.get(2)).
                isWednesday(repeat.get(3)).
                isThursday(repeat.get(4)).
                isFriday(repeat.get(5)).
                isSaturday(repeat.get(6)).
                build();

        int todoId = todoService.join(todo);
        todoService.updateGroupTodoId(todoId, todoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response("투두 생성 완료", new CreateTodoResponse(todoId, memberId, familyId)));

    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<?> handleMalformedJwtException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response("JWT 포맷이 올바른지 확인하세요", "MalformedJwtException"));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleSignatureException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response("JWT 포맷이 올바른지 확인하세요", "SignatureException"));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response("만료된 Token 입니다.", "ExpiredJwtException"));
    }

    @ExceptionHandler(ExpiredAccessTokenException.class)
    public ResponseEntity<?> handleExpiredAccessTokenException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response("만료된 Access Token 입니다.", "ExpiredAccessTokenException"));
    }


    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<?> handleEmptyResultDataAccessException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonFailResponse.response("요청한 리소스 정보가 없습니다.", "EmptyResultDataAccessException"));
    }

    @ExceptionHandler(ParseException.class)
    public ResponseEntity<?> handleParseException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response("생년월일 포맷이 yyyy-MM-dd인지 확인하세요", "ParseException"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response(e.getMessage(), e.toString()));
    }
}
