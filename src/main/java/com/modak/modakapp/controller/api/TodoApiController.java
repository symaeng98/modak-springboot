package com.modak.modakapp.controller.api;

import com.modak.modakapp.dto.response.CommonFailResponse;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.dto.response.todo.*;;
import com.modak.modakapp.jwt.TokenService;
import com.modak.modakapp.vo.todo.*;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.exception.token.ExpiredAccessTokenException;
import com.modak.modakapp.repository.date.TodoDateRepository;
import com.modak.modakapp.service.FamilyService;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.service.TodoDoneService;
import com.modak.modakapp.service.TodoService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

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
            @ApiResponse(code = 200, message = "할 일 등록에 성공하였습니다."),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @PostMapping("/new")
    public ResponseEntity create(@ApiParam(value = "todo 생성 정보 및 fromDate, toDate", required = true) @RequestBody CreateTodoVO createTodoVO) {
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


        // 반복 로직 ex) [0,0,0,0,1,0,0]
        List<Integer> repeat = createTodoVO.getRepeat();

        String repeatTag = todoService.getRepeatTag(repeat);
        // 반복 x
        if(repeatTag==null){
            endDate = startDate;
        }

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




//        WeekResponse wr = todoDateRepository.getCreateResponse(todo, dates, family);
        WeekResponse wr = todoDateRepository.findWeekColorsAndItemsAndGaugeByDateRange(createTodoVO.getFromDate(),createTodoVO.getToDate(), family);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response("투두 생성 완료", new CreateTodoResponse(todoId, wr)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "단일 todo 수정에 성공하였습니다."),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @PutMapping("/single/{id}")
    public ResponseEntity<?> updateTodo(@ApiParam(value = "단일 todo 수정 정보", required = true) @PathVariable("id") int todoId, @RequestBody UpdateTodoVO updateTodoVO){
        String accessToken = updateTodoVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        String title = updateTodoVO.getTitle();
        String memo = updateTodoVO.getMemo();
        Member member = memberService.findMember(updateTodoVO.getMemberId());
        String date = updateTodoVO.getDate();
        String timeTag = updateTodoVO.getTimeTag();
        Family family = member.getFamily();

        todoService.updateTodo(todoId, title,memo,member,date,timeTag);
        WeekResponse wr = todoDateRepository.findWeekColorsAndItemsAndGaugeByDateRange(updateTodoVO.getFromDate(), updateTodoVO.getToDate(), family);

        return ResponseEntity.ok(CommonSuccessResponse.response("수정 성공", wr));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "반복 todo - 단일 수정에 성공하였습니다."),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
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
            WeekResponse wr = todoDateRepository.findWeekColorsAndItemsAndGaugeByDateRange(updateTodoVO.getFromDate(), updateTodoVO.getToDate(), family);
            return ResponseEntity.ok(CommonSuccessResponse.response("수정 성공", new UpdateSingleTodoResponse(todoId,wr)));
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

        Date endDate = findTodo.getEndDate();
        Date yestDate = todoService.updateEndDateYest(todoId, date);
        Todo todo2 = Todo.builder().member(findTodo.getMember()).
                family(findTodo.getFamily()).
                title(findTodo.getTitle()).
                memo(findTodo.getMemo()).
                timeTag(findTodo.getTimeTag()).
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
        int afterTodoId = todoService.join(todo2);
        Date tomDate = todoService.updateStartDateTom(afterTodoId, date);
        System.out.println(yestDate+"\n"+tomDate);

        WeekResponse wr = todoDateRepository.findWeekColorsAndItemsAndGaugeByDateRange(updateTodoVO.getFromDate(), updateTodoVO.getToDate(), family);
        return ResponseEntity.ok(CommonSuccessResponse.response("수정 성공", new UpdateTodoResponse(newTodoId,afterTodoId,wr)));
    }
    @ApiResponses({
            @ApiResponse(code = 200, message = "반복 todo 이후 수정에 성공하였습니다."),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @PutMapping("/repeat/later/{id}")
    public ResponseEntity<?> updateRepeatLaterTodo(@PathVariable("id") int todoId, @RequestBody UpdateTodoVO updateTodoVO){
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

        todoService.updateEndDateYest(todoId,date);

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

        WeekResponse wr = todoDateRepository.findWeekColorsAndItemsAndGaugeByDateRange(updateTodoVO.getFromDate(), updateTodoVO.getToDate(), family);
        return ResponseEntity.ok(CommonSuccessResponse.response("수정 성공", new UpdateSingleTodoResponse(newTodoId,wr)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "요청한 정보를 성공적으로 불러왔습니다."),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @PostMapping("/week")
    public ResponseEntity<?> weekTodos(@RequestBody WeekVO weekVO){
        String accessToken = weekVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        int familyId = weekVO.getFamilyId();
        Family family = familyService.find(familyId);

        String fromDate = weekVO.getFromDate();
        String toDate = weekVO.getToDate();


        WeekResponse weekColorsAndItemsByDateRange = todoDateRepository.findWeekColorsAndItemsAndGaugeByDateRange(fromDate,toDate,family);

        return ResponseEntity.ok(CommonSuccessResponse.response("일주일치 정보", weekColorsAndItemsByDateRange));
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "완료/취소 처리에 성공했습니다"),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @PutMapping("/done/{id}")
    public ResponseEntity<?> done(@PathVariable("id") int todoId, @RequestBody DoneTodoVO doneTodoVO) {
        String accessToken = doneTodoVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        Todo todo = todoService.findTodo(todoId);
        Date date = Date.valueOf(doneTodoVO.getDate());
        Family family = todo.getFamily();
        int isDone = doneTodoVO.getIsDone();

        int todoDoneId = todoDoneService.updateIsDone(todo, date, isDone);


        WeekResponse wr = todoDateRepository.findWeekColorsAndItemsAndGaugeByDateRange(doneTodoVO.getFromDate(),doneTodoVO.getToDate(), family);
        return ResponseEntity.ok(CommonSuccessResponse.response("완료/취소 처리 성공", new UpdateSingleTodoResponse(todoId,wr)));
    }


    // repeat tag 유무에 따라 single, repeat 판단
    @ApiResponses({
            @ApiResponse(code = 200, message = "단일/반복 todo 단일 삭제를 성공했습니다."),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable("id") int todoId, @RequestBody DeleteRepeatTodoVO deleteRepeatTodoVO){
        String accessToken = deleteRepeatTodoVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        Todo findTodo = todoService.findTodo(todoId);

        Member member = findTodo.getMember();
        Family family = member.getFamily();

        // 단일 삭제면
        if(findTodo.getStartDate().equals(findTodo.getEndDate())){
            todoService.deleteSingleTodo(todoId);

            WeekResponse wr = todoDateRepository.findWeekColorsAndItemsAndGaugeByDateRange(deleteRepeatTodoVO.getFromDate(), deleteRepeatTodoVO.getToDate(), family);
            return ResponseEntity.ok(CommonSuccessResponse.response("삭제 성공", new DeleteSingleTodoResponse(todoId,wr)));
        }

        String title = findTodo.getTitle();
        String memo = findTodo.getMemo();
        Date date = Date.valueOf(deleteRepeatTodoVO.getDate());
        String timeTag = findTodo.getTimeTag();



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
                deletedAt(Timestamp.valueOf(LocalDateTime.now())). // 삭제 todo 생성
                build();

        Date endDate = findTodo.getEndDate();
        Date yestDate = todoService.updateEndDateYest(todoId, date);
        Todo todo2 = Todo.builder().member(findTodo.getMember()).
                family(findTodo.getFamily()).
                title(findTodo.getTitle()).
                memo(findTodo.getMemo()).
                timeTag(findTodo.getTimeTag()).
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
        int afterTodoId = todoService.join(todo2);
        Date tomDate = todoService.updateStartDateTom(afterTodoId, date);
        System.out.println(yestDate+"\n"+tomDate);

        WeekResponse wr = todoDateRepository.findWeekColorsAndItemsAndGaugeByDateRange(deleteRepeatTodoVO.getFromDate(), deleteRepeatTodoVO.getToDate(), family);
        return ResponseEntity.ok(CommonSuccessResponse.response("삭제 성공", new UpdateTodoResponse(newTodoId,afterTodoId,wr)));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "반복 todo - 이후 삭제를 성공했습니다."),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @DeleteMapping("/repeat/later/{id}")
    public ResponseEntity<?> deleteRepeatLaterTodo(@PathVariable("id") int todoId, @RequestBody DeleteRepeatTodoVO deleteRepeatTodoVO) {
        String accessToken = deleteRepeatTodoVO.getAccessToken().substring(7);
        tokenService.isAccessTokenExpired(accessToken);

        Todo findTodo = todoService.findTodo(todoId);
        Family family = findTodo.getFamily();

        todoService.updateEndDateYest(todoId,Date.valueOf(deleteRepeatTodoVO.getDate()));
        todoService.deleteByGroupTodoId(findTodo.getGroupTodoId(),Date.valueOf(deleteRepeatTodoVO.getDate()));

        WeekResponse wr = todoDateRepository.findWeekColorsAndItemsAndGaugeByDateRange(deleteRepeatTodoVO.getFromDate(), deleteRepeatTodoVO.getToDate(), family);
        return ResponseEntity.ok(CommonSuccessResponse.response("삭제 성공", new UpdateSingleTodoResponse(todoId,wr)));
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
