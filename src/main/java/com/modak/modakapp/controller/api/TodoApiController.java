package com.modak.modakapp.controller.api;

import com.modak.modakapp.DTO.CommonFailResponse;
import com.modak.modakapp.DTO.CommonSuccessResponse;
import com.modak.modakapp.DTO.Todo.*;
import com.modak.modakapp.Jwt.TokenService;
import com.modak.modakapp.VO.Todo.CreateTodoVO;
import com.modak.modakapp.VO.Todo.DoneTodoVO;
import com.modak.modakapp.VO.Todo.UpdateTodoVO;
import com.modak.modakapp.VO.Todo.WeekTodoVO;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.domain.TodoDone;
import com.modak.modakapp.exception.token.ExpiredAccessTokenException;
import com.modak.modakapp.repository.date.TodoDateRepository;
import com.modak.modakapp.service.FamilyService;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.service.TodoDoneService;
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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo")
@Slf4j
public class TodoApiController {
    private final TodoService todoService;
    private final MemberService memberService;

    private final TokenService tokenService;
    private final FamilyService familyService;

    private final TodoDateRepository todoDateRepository;

    private final TodoDoneService todoDoneService;

//    private final HttpServletResponse servletResponse;

    @ApiResponses({
            @ApiResponse(code = 201, message = "할 일 등록에 성공하였습니다."),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @PostMapping("/new")
    public ResponseEntity create(@RequestBody CreateTodoVO createTodoVO) {
        String accessToken = createTodoVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        // 담당자 가져오기
        int memberId = createTodoVO.getMemberId();
        Member findMember = memberService.findMember(memberId);

        // 가족 가져오기
        Family family = findMember.getFamily();
        int familyId = family.getId();


        // 날짜 변형
        Date startDate = Date.valueOf(createTodoVO.getDate());
        Date endDate = Date.valueOf("2025-01-01");


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
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response("투두 생성 완료", new CreateTodoResponse(todoId, memberId,familyId)));
    }

    @PutMapping("/single/{id}")
    public ResponseEntity<?> updateTodo(@PathVariable("id") int todoId, @RequestBody UpdateTodoVO updateTodoVO){
        String accessToken = updateTodoVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        String title = updateTodoVO.getTitle();
        String memo = updateTodoVO.getMemo();
        Member member = memberService.findMember(updateTodoVO.getMemberId());
        String date = updateTodoVO.getDate();
        String timeTag = updateTodoVO.getTimeTag();

        todoService.updateTodo(todoId, title,memo,member,date,timeTag);
        return ResponseEntity.ok(CommonSuccessResponse.response("업데이트에 성공하였습니다.", new UpdateTodoResponse(todoId)));
    }

    @PutMapping("/repeat/single/{id}")
    public ResponseEntity<?> updateRepeatTodo(@PathVariable("id") int todoId, @RequestBody UpdateTodoVO updateTodoVO){
        String accessToken = updateTodoVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        Todo findTodo = todoService.findTodo(todoId);

        String title = updateTodoVO.getTitle();
        String memo = updateTodoVO.getMemo();
        Member member = memberService.findMember(updateTodoVO.getMemberId());
        Date date = Date.valueOf(updateTodoVO.getDate());
        String timeTag = updateTodoVO.getTimeTag();

        Family family = member.getFamily();

        // 이미 수정한 적이 있으면
        if(findTodo.getStartDate().equals(findTodo.getEndDate())){
            todoService.updateTodo(todoId, title,memo,member, updateTodoVO.getDate(), timeTag);
            return ResponseEntity.ok(CommonSuccessResponse.response("업데이트에 성공하였습니다.", new UpdateTodoResponse(todoId)));
        }

        Todo todo = Todo.builder().member(member).
                family(family).
                title(title).
                memo(memo).
                timeTag(timeTag).
                startDate(date).
                endDate(date).
                repeatTag(findTodo.getRepeatTag()).
                groupTodoId(findTodo.getGroupTodoId()).
                isSunday(findTodo.getIsSunday()).
                isMonday(findTodo.getIsMonday()).
                isTuesday(findTodo.getIsTuesday()).
                isWednesday(findTodo.getIsWednesday()).
                isThursday(findTodo.getIsThursday()).
                isFriday(findTodo.getIsFriday()).
                isSaturday(findTodo.getIsSaturday()).
                build();

        int newTodoId = todoService.join(todo);
        return ResponseEntity.ok(CommonSuccessResponse.response("업데이트에 성공하였습니다.", new UpdateTodoResponse(newTodoId)));
    }

    @PutMapping("/repeat/later/{id}")
    public ResponseEntity<?> updateReapeatTodo(@PathVariable("id") int todoId, @RequestBody UpdateTodoVO updateTodoVO){
        String accessToken = updateTodoVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        Todo findTodo = todoService.findTodo(todoId);

        String title = updateTodoVO.getTitle();
        String memo = updateTodoVO.getMemo();
        Member member = memberService.findMember(updateTodoVO.getMemberId());
        Date date = Date.valueOf(updateTodoVO.getDate());
        Date endDate = Date.valueOf("2025-01-01");
        String timeTag = updateTodoVO.getTimeTag();
        Family family = member.getFamily();

        todoService.updateEndDate(todoId,date);

        Todo todo = Todo.builder().member(member).
                family(family).
                title(title).
                memo(memo).
                timeTag(timeTag).
                startDate(date).
                endDate(endDate).
                repeatTag(findTodo.getRepeatTag()).
                groupTodoId(findTodo.getGroupTodoId()).
                isSunday(findTodo.getIsSunday()).
                isMonday(findTodo.getIsMonday()).
                isTuesday(findTodo.getIsTuesday()).
                isWednesday(findTodo.getIsWednesday()).
                isThursday(findTodo.getIsThursday()).
                isFriday(findTodo.getIsFriday()).
                isSaturday(findTodo.getIsSaturday()).
                build();

        int newTodoId = todoService.join(todo);
        return ResponseEntity.ok(CommonSuccessResponse.response("업데이트에 성공하였습니다.", new UpdateTodoResponse(newTodoId)));
    }

    @GetMapping("/week")
    public ResponseEntity<?> weekTodos(@RequestBody WeekTodoVO weekTodoVO){
        String accessToken = weekTodoVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        int familyId = weekTodoVO.getFamilyId();
        Family family = familyService.find(familyId);

        String fromDate = weekTodoVO.getFromDate();
        String toDate = weekTodoVO.getToDate();

        List<String> dates = new ArrayList<>();
        LocalDate start = LocalDate.parse(fromDate);
        LocalDate end = LocalDate.parse(toDate);

        Stream.iterate(start, date->date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start,end)+1)
                .forEach(d -> {
                    dates.add(String.valueOf(d));
                });

        WeekResponse weekColorsAndItemsByDateRange = todoDateRepository.findWeekColorsAndItemsByDateRange(dates,family);
        int gauge = todoService.getGauge(family);
        weekColorsAndItemsByDateRange.setGauge(gauge);

        return ResponseEntity.ok(CommonSuccessResponse.response("업데이트에 성공하였습니다.", weekColorsAndItemsByDateRange));
    }


    @PutMapping("/done/{id}")
    public ResponseEntity<?> done(@PathVariable("id") int todoId, @RequestBody DoneTodoVO doneTodoVO) {
        Todo todo = todoService.findTodo(todoId);
        Date date = Date.valueOf(doneTodoVO.getDate());
        int isDone = doneTodoVO.getIsDone();

        int todoDoneId = todoDoneService.updateIsDone(todo, date, isDone);

        return ResponseEntity.ok(CommonSuccessResponse.response("완료/취소 요청을 처리했습니다.", new DoneTodoResponse(todoId,todoDoneId)));
    }



    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<?> handleMalformedJwtException(MalformedJwtException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response("JWT 포맷이 올바른지 확인하세요", "MalformedJwtException"));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleSignatureException(SignatureException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response("JWT 포맷이 올바른지 확인하세요", "SignatureException"));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response("만료된 Token 입니다.", "ExpiredJwtException"));
    }

    @ExceptionHandler(ExpiredAccessTokenException.class)
    public ResponseEntity<?> handleExpiredAccessTokenException(ExpiredAccessTokenException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response("만료된 Access Token 입니다.", "ExpiredAccessTokenException"));
    }


    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<?> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonFailResponse.response("요청한 리소스 정보가 없습니다.", "EmptyResultDataAccessException"));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response(e.getMessage(), e.toString()));
    }
}
