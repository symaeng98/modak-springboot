package com.modak.modakapp.controller;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.dto.response.CommonFailResponse;
import com.modak.modakapp.dto.response.CommonSuccessResponse;
import com.modak.modakapp.dto.response.todo.CreateTodoResponse;
import com.modak.modakapp.dto.response.todo.UpdateSingleTodoResponse;
import com.modak.modakapp.dto.response.todo.WeekResponse;
import com.modak.modakapp.exception.token.ExpiredAccessTokenException;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.service.TodoDoneService;
import com.modak.modakapp.service.TodoService;
import com.modak.modakapp.utils.jwt.TokenService;
import com.modak.modakapp.utils.todo.TodoUtil;
import com.modak.modakapp.vo.todo.*;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo")
@Slf4j
public class TodoController {
    private final TodoService todoService;
    private final MemberService memberService;
    private final TokenService tokenService;
    private final TodoDoneService todoDoneService;
    private final TodoUtil todoUtil;
    private final String ACCESS_TOKEN = "Access-Token";

    @ApiResponses({
            @ApiResponse(code = 201, message = "할 일 등록에 성공하였습니다."),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @PostMapping()
    public ResponseEntity<CommonSuccessResponse<CreateTodoResponse>> createTodo(
            @ApiParam(value = "todo 생성 정보 및 fromDate, toDate", required = true)
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @RequestBody CreateTodoVO createTodoVO
    ) {
        tokenService.validateAccessTokenExpired(accessToken);

        // 담당자 가져오기
        int memberId = createTodoVO.getMemberId();
        Member memberWithFamily = memberService.findMemberWithFamily(memberId);

        // 가족 가져오기
        Family family = memberWithFamily.getFamily();

        // 날짜 변형
        Date startDate = Date.valueOf(createTodoVO.getDate());
        Date endDate = Date.valueOf("2025-01-01");

        // 반복 로직 ex) [0,0,0,0,1,0,0]
        List<Integer> repeat = createTodoVO.getRepeat();

        String repeatTag = todoUtil.getRepeatTag(repeat);
        // 반복 x
        if (repeatTag == null) {
            endDate = startDate;
        }

        Todo todo = Todo.builder()
                .member(memberWithFamily)
                .title(createTodoVO.getTitle())
                .memo(createTodoVO.getMemo())
                .timeTag(createTodoVO.getTimeTag())
                .startDate(startDate)
                .endDate(endDate)
                .repeatTag(repeatTag)
                .isSunday(repeat.get(0))
                .isMonday(repeat.get(1))
                .isTuesday(repeat.get(2))
                .isWednesday(repeat.get(3))
                .isThursday(repeat.get(4))
                .isFriday(repeat.get(5))
                .isSaturday(repeat.get(6))
                .build();
        todo.changeFamily(family);

        int todoId = todoService.join(todo);
        todoService.updateGroupTodoId(todoId, todoId);

        WeekResponse weekColorsAndItemsByDateRange = todoService.findWeekColorsAndItemsAndGaugeByDateRange(createTodoVO.getFromDate(), createTodoVO.getToDate(), family);
        weekColorsAndItemsByDateRange.setGauge(todoDoneService.findNumOfTodoDone(family.getId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonSuccessResponse<>("투두 생성 완료", new CreateTodoResponse(todoId, weekColorsAndItemsByDateRange), true));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "todo 수정에 성공하였습니다."),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @PutMapping("/{id}")
    public ResponseEntity<CommonSuccessResponse<WeekResponse>> updateTodo(
            @ApiParam(value = "todo 수정 정보", required = true)
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @PathVariable("id") int todoId,
            @RequestBody UpdateTodoVO updateTodoVO
    ) {
        tokenService.validateAccessTokenExpired(accessToken);

        // 담당자
        Member memberWithFamily = memberService.findMemberWithFamily(updateTodoVO.getMemberId());
        Family family = memberWithFamily.getFamily();

        // 반복 로직 ex) [0,0,0,0,1,0,0]
        List<Integer> repeat = updateTodoVO.getRepeat();

        String repeatTag = todoUtil.getRepeatTag(repeat);
        // 반복 x
        if (repeatTag == null) {
            todoService.updateSingleTodo(todoId, updateTodoVO, memberWithFamily);
        } else { // 반복
            if (updateTodoVO.getIsAfterUpdate() == 1) { // 이후 모두 수정
                todoService.updateRepeatTodoAfter(todoId, updateTodoVO, memberWithFamily);
            } else { // 단일 이벤트 수정
                todoService.updateRepeatTodoSingle(todoId, updateTodoVO, memberWithFamily);
            }
        }

        WeekResponse weekColorsAndItemsByDateRange = todoService.findWeekColorsAndItemsAndGaugeByDateRange(updateTodoVO.getFromDate(), updateTodoVO.getToDate(), family);
        weekColorsAndItemsByDateRange.setGauge(todoDoneService.findNumOfTodoDone(family.getId()));

        return ResponseEntity.ok(new CommonSuccessResponse<>("수정 성공", weekColorsAndItemsByDateRange, true));
    }

//    @ApiResponses({
//            @ApiResponse(code = 200, message = "반복 todo - 단일 수정에 성공하였습니다."),
//            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
//            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
//    })
//    @PutMapping("/repeat/single/{id}")
//    public ResponseEntity<?> updateRepeatTodo(
//            @RequestHeader AccessTokenVO accessTokenVO,
//            @PathVariable("id") int todoId,
//            @RequestBody UpdateTodoVO updateTodoVO
//    ) {
//        String accessToken = accessTokenVO.getAccessToken().substring(7);
//        tokenService.validateAccessTokenExpired(accessToken);
//
//        Todo findTodo = todoService.findTodo(todoId);
//
//        String title = updateTodoVO.getTitle();
//        String memo = updateTodoVO.getMemo();
//        Member member = memberService.findMember(updateTodoVO.getMemberId());
//        Date date = Date.valueOf(updateTodoVO.getDate());
//        String timeTag = updateTodoVO.getTimeTag();
//
//        Family family = member.getFamily();
//
//        // 이미 수정한 적이 있으면
//        if (findTodo.getStartDate().equals(findTodo.getEndDate())) {
//            todoService.updateTodo(todoId, title, memo, member, updateTodoVO.getDate(), timeTag);
//            WeekResponse wr = todoDateRepository.findWeekColorsAndItemsAndGaugeByDateRange(updateTodoVO.getFromDate(), updateTodoVO.getToDate(), family);
//            return ResponseEntity.ok(CommonSuccessResponse.response("수정 성공", new UpdateSingleTodoResponse(todoId, wr)));
//        }
//
//        Todo todo = Todo.builder().member(member).
//                family(family).
//                title(title).
//                memo(memo).
//                timeTag(timeTag).
//                startDate(date).
//                endDate(date).
//                repeatTag(findTodo.getRepeatTag()).
//                groupTodoId(findTodo.getGroupTodoId()).
//                isSunday(findTodo.getIsSunday()).
//                isMonday(findTodo.getIsMonday()).
//                isTuesday(findTodo.getIsTuesday()).
//                isWednesday(findTodo.getIsWednesday()).
//                isThursday(findTodo.getIsThursday()).
//                isFriday(findTodo.getIsFriday()).
//                isSaturday(findTodo.getIsSaturday()).
//                build();
//
//        Date endDate = findTodo.getEndDate();
//        Date yestDate = todoService.updateEndDateYest(todoId, date);
//        Todo todo2 = Todo.builder().member(findTodo.getMember()).
//                family(findTodo.getFamily()).
//                title(findTodo.getTitle()).
//                memo(findTodo.getMemo()).
//                timeTag(findTodo.getTimeTag()).
//                startDate(date).
//                endDate(endDate).
//                repeatTag(findTodo.getRepeatTag()).
//                groupTodoId(findTodo.getGroupTodoId()).
//                isSunday(findTodo.getIsSunday()).
//                isMonday(findTodo.getIsMonday()).
//                isTuesday(findTodo.getIsTuesday()).
//                isWednesday(findTodo.getIsWednesday()).
//                isThursday(findTodo.getIsThursday()).
//                isFriday(findTodo.getIsFriday()).
//                isSaturday(findTodo.getIsSaturday()).
//                build();
//
//        int newTodoId = todoService.join(todo);
//        int afterTodoId = todoService.join(todo2);
//        Date tomDate = todoService.updateStartDateTom(afterTodoId, date);
//        System.out.println(yestDate + "\n" + tomDate);
//
//        WeekResponse wr = todoDateRepository.findWeekColorsAndItemsAndGaugeByDateRange(updateTodoVO.getFromDate(), updateTodoVO.getToDate(), family);
//        return ResponseEntity.ok(CommonSuccessResponse.response("수정 성공", new UpdateTodoResponse(newTodoId, afterTodoId, wr)));
//    }
//
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "반복 todo 이후 수정에 성공하였습니다."),
//            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
//            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
//    })
//    @PutMapping("/repeat/later/{id}")
//    public ResponseEntity<?> updateRepeatLaterTodo(
//            @PathVariable("id") int todoId,
//            @RequestBody UpdateTodoVO updateTodoVO,
//            @RequestHeader AccessTokenVO accessTokenVO
//    ) {
//        String accessToken = accessTokenVO.getAccessToken().substring(7);
//        tokenService.validateAccessTokenExpired(accessToken);
//
//        Todo findTodo = todoService.findTodo(todoId);
//
//        String title = updateTodoVO.getTitle();
//        String memo = updateTodoVO.getMemo();
//        Member member = memberService.findMember(updateTodoVO.getMemberId());
//        Date date = Date.valueOf(updateTodoVO.getDate());
//        Date endDate = Date.valueOf("2025-01-01");
//        String timeTag = updateTodoVO.getTimeTag();
//        Family family = member.getFamily();
//
//        todoService.updateEndDateYest(todoId, date);
//
//        Todo todo = Todo.builder().member(member).
//                family(family).
//                title(title).
//                memo(memo).
//                timeTag(timeTag).
//                startDate(date).
//                endDate(endDate).
//                repeatTag(findTodo.getRepeatTag()).
//                groupTodoId(findTodo.getGroupTodoId()).
//                isSunday(findTodo.getIsSunday()).
//                isMonday(findTodo.getIsMonday()).
//                isTuesday(findTodo.getIsTuesday()).
//                isWednesday(findTodo.getIsWednesday()).
//                isThursday(findTodo.getIsThursday()).
//                isFriday(findTodo.getIsFriday()).
//                isSaturday(findTodo.getIsSaturday()).
//                build();
//
//        int newTodoId = todoService.join(todo);
//
//        WeekResponse wr = todoDateRepository.findWeekColorsAndItemsAndGaugeByDateRange(updateTodoVO.getFromDate(), updateTodoVO.getToDate(), family);
//        return ResponseEntity.ok(CommonSuccessResponse.response("수정 성공", new UpdateSingleTodoResponse(newTodoId, wr)));
//    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "요청한 정보를 성공적으로 불러왔습니다."),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @PostMapping("/from-to-date")
    public ResponseEntity<CommonSuccessResponse<WeekResponse>> getTodosByFromToDate(
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @RequestBody FromToDateVO fromToDateVO
    ) {
        String subAccessToken = tokenService.validateAccessTokenExpired(accessToken);

        // 회원 id 가져와서 회원 찾기
        int memberId = tokenService.getMemberId(subAccessToken);
        Member memberWithFamily = memberService.findMemberWithFamily(memberId);

        Family family = memberWithFamily.getFamily();

        WeekResponse weekColorsAndItemsByDateRange = todoService.findWeekColorsAndItemsAndGaugeByDateRange(fromToDateVO.getFromDate(), fromToDateVO.getToDate(), family);
        weekColorsAndItemsByDateRange.setGauge(todoDoneService.findNumOfTodoDone(family.getId()));

        return ResponseEntity.ok(new CommonSuccessResponse<>("일주일치 todo 정보 불러오기 성공", weekColorsAndItemsByDateRange, true));
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "완료/취소 처리에 성공했습니다"),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @PutMapping("/done/{id}")
    public ResponseEntity<CommonSuccessResponse<UpdateSingleTodoResponse>> done(
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @PathVariable("id") int todoId,
            @RequestBody DoneTodoVO doneTodoVO
    ) {
        tokenService.validateAccessTokenExpired(accessToken);

        Todo todo = todoService.findTodoWithMemberAndFamily(todoId);
        Date date = Date.valueOf(doneTodoVO.getDate());
        Family family = todo.getFamily();
        int isDone = doneTodoVO.getIsDone();

        todoDoneService.updateIsDone(todo, date, isDone);

        WeekResponse weekColorsAndItemsByDateRange = todoService.findWeekColorsAndItemsAndGaugeByDateRange(doneTodoVO.getFromDate(), doneTodoVO.getToDate(), family);
        weekColorsAndItemsByDateRange.setGauge(todoDoneService.findNumOfTodoDone(family.getId()));

        return ResponseEntity.ok(new CommonSuccessResponse<>("완료/취소 처리 성공", new UpdateSingleTodoResponse(todoId, weekColorsAndItemsByDateRange), true));
    }


    // repeat tag 유무에 따라 single, repeat 판단
    @ApiResponses({
            @ApiResponse(code = 200, message = "단일/반복 todo 단일 삭제를 성공했습니다."),
            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonSuccessResponse<WeekResponse>> deleteTodo(
            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
            @PathVariable("id") int todoId,
            @RequestBody DeleteTodoVO deleteTodoVO
    ) {
        String subAccessToken = tokenService.validateAccessTokenExpired(accessToken);

        // 회원 id 가져와서 회원과 가족 찾기
        int memberId = tokenService.getMemberId(subAccessToken);
        Member memberWithFamily = memberService.findMemberWithFamily(memberId);
        Family family = memberWithFamily.getFamily();

        Todo todo = todoService.findTodoWithMemberAndFamily(todoId);

        // 이후 모두 삭제면
        if (deleteTodoVO.getIsAfterDelete() == 1) {
            todoService.deleteRepeatTodoAfter(todoId, deleteTodoVO);
        } else { // 단일 이벤트 삭제면
            // 단일 삭제면
            if (todo.getStartDate().equals(todo.getEndDate())) {
                todoService.deleteSingleTodo(todoId);
            } else { // 반복에서 단일 삭제면
                todoService.deleteRepeatTodoSingle(todoId, deleteTodoVO);
            }
        }

        WeekResponse weekColorsAndItemsByDateRange = todoService.findWeekColorsAndItemsAndGaugeByDateRange(deleteTodoVO.getFromDate(), deleteTodoVO.getToDate(), family);
        weekColorsAndItemsByDateRange.setGauge(todoDoneService.findNumOfTodoDone(family.getId()));

        return ResponseEntity.ok(new CommonSuccessResponse<>("삭제 성공", weekColorsAndItemsByDateRange, true));
    }

//    @ApiResponses({
//            @ApiResponse(code = 200, message = "반복 todo - 이후 삭제를 성공했습니다."),
//            @ApiResponse(code = 401, message = "Access Token이 만료되었습니다.(ExpiredAccessTokenException)"),
//            @ApiResponse(code = 400, message = "1. JWT 포맷이 올바른지 확인하세요.(MalformedJwtException).\n2. JWT 포맷이 올바른지 확인하세요.(SignatureException)\n3. 에러 메시지를 확인하세요. 어떤 에러가 떴는지 저도 잘 모릅니다.."),
//    })
//    @DeleteMapping("/repeat/later/{id}")
//    public ResponseEntity<?> deleteRepeatLaterTodo(
//            @RequestHeader AccessTokenVO accessTokenVO,
//            @PathVariable("id") int todoId,
//            @RequestBody DeleteTodoVO deleteTodoVO
//    ) {
//        String accessToken = accessTokenVO.getAccessToken().substring(7);
//        tokenService.validateAccessTokenExpired(accessToken);
//
//        Todo findTodo = todoService.findTodo(todoId);
//        Family family = findTodo.getFamily();
//
//        todoService.updateEndDateYest(todoId, Date.valueOf(deleteRepeatTodoVO.getDate()));
//        todoService.deleteByGroupTodoId(findTodo.getGroupTodoId(), Date.valueOf(deleteRepeatTodoVO.getDate()));
//
//        WeekResponse wr = todoDateRepository.findWeekColorsAndItemsAndGaugeByDateRange(deleteRepeatTodoVO.getFromDate(), deleteRepeatTodoVO.getToDate(), family);
//        return ResponseEntity.ok(CommonSuccessResponse.response("삭제 성공", new UpdateSingleTodoResponse(todoId, wr)));
//    }

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
