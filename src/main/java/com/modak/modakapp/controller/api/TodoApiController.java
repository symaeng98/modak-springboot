package com.modak.modakapp.controller.api;

import com.modak.modakapp.DTO.CommonFailResponse;
import com.modak.modakapp.DTO.CommonSuccessResponse;
import com.modak.modakapp.DTO.Todo.CreateTodoResponse;
import com.modak.modakapp.DTO.Todo.MDRepeatTagDto;
import com.modak.modakapp.Jwt.TokenService;
import com.modak.modakapp.VO.Todo.CreateTodoVO;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.domain.metadata.MDRepeatTag;
import com.modak.modakapp.exception.ExpiredAccessTokenException;
import com.modak.modakapp.exception.NotAuthorizedMemberException;
import com.modak.modakapp.service.FamilyService;
import com.modak.modakapp.service.MemberService;
import com.modak.modakapp.service.TodoService;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class TodoApiController {
    private final TodoService todoService;
    private final MemberService memberService;
    private final FamilyService familyService;

    private final TokenService tokenService;

    private final HttpServletResponse servletResponse;

    @PostMapping("/new")
    public ResponseEntity create(@RequestBody CreateTodoVO createTodoVO){
        String accessToken = createTodoVO.getAccessToken().substring(7);
        try{
            boolean isExpired = tokenService.isAccessTokenExpired(accessToken);
            Date startDate = new SimpleDateFormat("yyyyMMdd").parse(createTodoVO.getStartDate().replace("-",""));
            java.sql.Date startSQLDate = new java.sql.Date(startDate.getTime());
            Date endDate = new SimpleDateFormat("yyyyMMdd").parse(createTodoVO.getEndDate().replace("-",""));
            java.sql.Date endSqlDate = new java.sql.Date(endDate.getTime());

            // 멤버 가져오기
            int memberId = tokenService.getMemberId(accessToken);
            Member findMember = memberService.findMember(memberId);

            // 가족 가져오기
            Family family = findMember.getFamily();
            int familyId = family.getId();
            System.out.println(createTodoVO.getRepeatTag().get("repeat"));

            Todo todo = Todo.builder().member(findMember).
                    family(family).
                    title(createTodoVO.getTitle()).
                    describe(createTodoVO.getDescribe()).
                    timeTag(createTodoVO.getTimeTag()).
                    startDate(startSQLDate).
                    repeatTag(new MDRepeatTag(createTodoVO.getRepeatTag().get("repeat"))).
                    endDate(endSqlDate).build();
            System.out.println("todo.getRepeatTag().getRepeatDays().getClass() = " + todo.getRepeatTag().getRepeat().getClass());
            System.out.println(todo.getRepeatTag().getRepeat());
            int todoId = todoService.join(todo);

            Todo findTodo = todoService.findTodo(todoId);
            System.out.println(findTodo.getRepeatTag().getRepeat().get(0));

            return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response("투두 생성 완료", new CreateTodoResponse(todoId,memberId, familyId)));
        }catch (ExpiredAccessTokenException | NotAuthorizedMemberException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response(e.getMessage()));
        }catch (ParseException e) {
            return ResponseEntity.ok(CommonFailResponse.response("생년월일 포맷이 yyyy-MM-dd인지 확인하세요"));
        }catch (MalformedJwtException | SignatureException e){
            return ResponseEntity.ok(CommonFailResponse.response("JWT 포맷이 올바른지 확인하세요"));
        }
    }
}
