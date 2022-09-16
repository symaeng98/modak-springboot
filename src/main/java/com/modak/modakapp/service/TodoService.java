package com.modak.modakapp.service;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.domain.TodoDone;
import com.modak.modakapp.dto.response.todo.TodoResponse;
import com.modak.modakapp.dto.todo.TodoDTO;
import com.modak.modakapp.exception.todo.NoSuchTodoException;
import com.modak.modakapp.repository.TodoDoneRepository;
import com.modak.modakapp.repository.TodoRepository;
import com.modak.modakapp.utils.todo.TodoUtil;
import com.modak.modakapp.vo.todo.DeleteTodoVO;
import com.modak.modakapp.vo.todo.UpdateTodoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoDoneRepository todoDoneRepository;
    private final TodoUtil todoUtil;

    @Transactional
    public int join(Todo todo) {
        todoRepository.save(todo);
        return todo.getId();
    }

    public Todo getTodoWithMemberAndFamily(int todoId) {
        return todoRepository.findById(todoId).orElseThrow(() -> new NoSuchTodoException("todo가 존재하지 않습니다."));
    }


    @Transactional
    public void updateGroupTodoId(Todo todo, int groupTodoId) {
        todo.changeGroupTodoId(groupTodoId);
    }

    @Transactional
    public void updateSingleTodo(int todoId, UpdateTodoVO updateTodoVO, Member member) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new NoSuchTodoException("todo가 존재하지 않습니다."));
        todo.changeSingleTodo(
                updateTodoVO.getTitle(),
                updateTodoVO.getMemo(),
                member,
                Date.valueOf(updateTodoVO.getDate()),
                updateTodoVO.getTimeTag()
        );
    }

    // 1. 수정한 날이 반복 todo의 시작 날
    // 2. 수정한 날이 반복 todo의 종료 날
    // 3. 일반적인 반복 todo 중 하루
    @Transactional
    public void updateRepeatTodoSingle(int todoId, UpdateTodoVO updateTodoVO, Member member) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new NoSuchTodoException("todo가 존재하지 않습니다."));

        String title = updateTodoVO.getTitle();
        String memo = updateTodoVO.getMemo();
        Date date = Date.valueOf(updateTodoVO.getDate());
        String timeTag = updateTodoVO.getTimeTag();

        Family family = member.getFamily();

        // 이미 수정한 적이 있으면
        if (todo.getStartDate().equals(todo.getEndDate())) {
            todo.changeSingleTodo(title, memo, member, date, timeTag);
            return;
        }

        // 수정하기 전 todo의 시작 날
        Date todoStartDate = todo.getStartDate();
        // 수정하기 전 todo의 마지막 날
        Date todoEndDate = todo.getEndDate();
        // 수정할 날의 할 일 생성
        Todo newSingleTodo = Todo.builder()
                .member(member)
                .family(family)
                .title(title)
                .memo(memo)
                .timeTag(timeTag)
                .startDate(date)
                .endDate(date)
                .repeatTag(todo.getRepeatTag())
                .groupTodoId(todo.getGroupTodoId())
                .isSunday(todo.getIsSunday())
                .isMonday(todo.getIsMonday())
                .isTuesday(todo.getIsTuesday())
                .isWednesday(todo.getIsWednesday())
                .isThursday(todo.getIsThursday())
                .isFriday(todo.getIsFriday())
                .isSaturday(todo.getIsSaturday())
                .build();
        todoRepository.save(newSingleTodo);

        if (todoEndDate.equals(date)) {
            // 종료일을 전 날로 수정
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            cal.add(Calendar.DATE, -1);
            Date newBeforeDate = new Date(cal.getTime().getTime());
            todo.changeEndDate(newBeforeDate);
            return;
        }

        // 시작일을 다음 날로 수정
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        Date newAfterDate = new Date(cal.getTime().getTime());
        todo.changeStartDate(newAfterDate);

        // 시작 날을 수정하는 경우
        if (todoStartDate.equals(date)) {
            return;
        }

        // 반복 중 하루 수정
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        Date newBeforeDate = new Date(cal.getTime().getTime());

        Todo newBeforeTodo = Todo.builder()
                .member(todo.getMember())
                .family(family)
                .title(todo.getTitle())
                .memo(todo.getMemo())
                .timeTag(todo.getTimeTag())
                .startDate(todoStartDate)
                .endDate(newBeforeDate)
                .repeatTag(todo.getRepeatTag())
                .groupTodoId(todo.getGroupTodoId())
                .isSunday(todo.getIsSunday())
                .isMonday(todo.getIsMonday())
                .isTuesday(todo.getIsTuesday())
                .isWednesday(todo.getIsWednesday())
                .isThursday(todo.getIsThursday())
                .isFriday(todo.getIsFriday())
                .isSaturday(todo.getIsSaturday())
                .build();
        todoRepository.save(newBeforeTodo);
    }

    @Transactional
    public void updateRepeatTodoAfter(int todoId, UpdateTodoVO updateTodoVO, Member member) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new NoSuchTodoException("todo가 존재하지 않습니다."));

        Date date = Date.valueOf(updateTodoVO.getDate());

        int groupTodoId = todo.getGroupTodoId();
        List<Todo> todos = todoRepository.findAllByGroupIdAndDate(date, groupTodoId);

        for (Todo t : todos) {
            // 시작 일은 date 보다 전인데 종료일은 date 보다 뒤인 경우
            if (t.getStartDate().before(date)) {
                // 종료일이 date 전날인 todo 생성
                Calendar cal = new GregorianCalendar();
                cal.setTime(date);
                cal.add(Calendar.DATE, -1);
                Date newBeforeDate = new Date(cal.getTime().getTime());

                Todo newBeforeTodo = Todo.builder()
                        .member(t.getMember())
                        .family(t.getFamily())
                        .title(t.getTitle())
                        .memo(t.getMemo())
                        .timeTag(t.getTimeTag())
                        .startDate(t.getStartDate())
                        .endDate(newBeforeDate)
                        .repeatTag(t.getRepeatTag())
                        .groupTodoId(t.getGroupTodoId())
                        .isSunday(t.getIsSunday())
                        .isMonday(t.getIsMonday())
                        .isTuesday(t.getIsTuesday())
                        .isWednesday(t.getIsWednesday())
                        .isThursday(t.getIsThursday())
                        .isFriday(t.getIsFriday())
                        .isSaturday(t.getIsSaturday())
                        .build();
                todoRepository.save(newBeforeTodo);

                t.changeStartDate(date);
            }
            // 변경 사항 저장
            t.changeRepeatTodo(
                    updateTodoVO.getTitle(),
                    updateTodoVO.getMemo(),
                    member,
                    updateTodoVO.getTimeTag()
            );
        }
    }

    @Transactional
    public void deleteSingleTodo(Todo todo) {
        todo.removeTodo(Timestamp.valueOf(LocalDateTime.now()));
    }

    @Transactional
    public void deleteRepeatTodoSingle(Todo todo, DeleteTodoVO deleteTodoVO) {
        // 삭제할 날짜
        Date date = Date.valueOf(deleteTodoVO.getDate());

        // 이미 수정한 적이 있으면
        if (todo.getStartDate().equals(todo.getEndDate())) {
            todo.removeTodo(Timestamp.valueOf(LocalDateTime.now()));
            return;
        }

        // 수정하기 전 todo의 시작 날
        Date todoStartDate = todo.getStartDate();
        // 수정하기 전 todo의 마지막 날
        Date todoEndDate = todo.getEndDate();

        // 마지막 날을 삭제하는 경우
        if (todoEndDate.equals(date)) {
            // 종료일을 전 날로 수정
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            cal.add(Calendar.DATE, -1);
            Date newBeforeDate = new Date(cal.getTime().getTime());
            todo.changeEndDate(newBeforeDate);
            return;
        }

        // 시작일을 다음 날로 수정
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        Date newAfterDate = new Date(cal.getTime().getTime());
        todo.changeStartDate(newAfterDate);

        // 시작 날을 삭제하는 경우
        if (todoStartDate.equals(date)) {
            return;
        }

        // 반복 중 하루 삭제
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        Date newBeforeDate = new Date(cal.getTime().getTime());

        Todo newBeforeTodo = Todo.builder()
                .member(todo.getMember())
                .family(todo.getFamily())
                .title(todo.getTitle())
                .memo(todo.getMemo())
                .timeTag(todo.getTimeTag())
                .startDate(todoStartDate)
                .endDate(newBeforeDate)
                .repeatTag(todo.getRepeatTag())
                .groupTodoId(todo.getGroupTodoId())
                .isSunday(todo.getIsSunday())
                .isMonday(todo.getIsMonday())
                .isTuesday(todo.getIsTuesday())
                .isWednesday(todo.getIsWednesday())
                .isThursday(todo.getIsThursday())
                .isFriday(todo.getIsFriday())
                .isSaturday(todo.getIsSaturday())
                .build();
        todoRepository.save(newBeforeTodo);
    }

    @Transactional
    public void deleteRepeatTodoAfter(Todo todo, DeleteTodoVO deleteTodoVO) {
        Date date = Date.valueOf(deleteTodoVO.getDate());

        int groupTodoId = todo.getGroupTodoId();
        List<Todo> todos = todoRepository.findAllByGroupIdAndDate(date, groupTodoId);

        for (Todo t : todos) {
            // 시작 일은 date 보다 전인데 종료일은 date 보다 뒤인 경우
            if (t.getStartDate().before(date)) {
                // 종료일을 date 전날로 수정
                Calendar cal = new GregorianCalendar();
                cal.setTime(date);
                cal.add(Calendar.DATE, -1);
                Date newBeforeDate = new Date(cal.getTime().getTime());

                t.changeEndDate(newBeforeDate);
                continue;
            }
            t.removeTodo(Timestamp.valueOf(LocalDateTime.now()));
        }
    }

//    @Transactional
//    public void deleteByGroupTodoId(int groupTodoId, Date date) {
//        List<Todo> todoList = todoRepository.findAllByGroupId(groupTodoId);
//        if (todoList.size() == 1) { // 반복 투두가 하나면
//            Todo todo = todoList.get(0);
//            updateEndDateYest(todo.getId(), date);
//            // 고민: row 하나 만들어서 delete 해야되나?
//        } else {
//            System.out.println(todoList.size());
//            todoList.forEach(t -> {
//                // date보다 나중에 있는 일정들이면 삭제
//                if (t.getStartDate().after(date)) {
//                    t.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
//                    List<TodoDone> todoDones = t.getTodoDone();
//                    todoDones.forEach(td -> {
//                        if (td.getDate().after(date)) {
//                            td.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
//                        }
//                    });
//                }
//            });
//        }
//    }

    // 색깔, to-do 한 번에 가져오기
    // 추후 수정...
    public TodoResponse findColorsAndItemsAndGaugeByDateRange(String fromDate, String toDate, Family family) {
        // 시작일 ~ 종료일 String List 로 변환
        List<String> dates = todoUtil.getFromToDateList(fromDate, toDate);

        Map<String, List<String>> colorListByDate = new HashMap<>();
        Map<String, List<TodoDTO>> todoListByDate = new HashMap<>();
        Date sqlFromDate = Date.valueOf(fromDate);
        Date sqlToDate = Date.valueOf(toDate);
        List<Todo> todoList = todoRepository.findAllByFromDateAndToDate(sqlFromDate, sqlToDate, family.getId());

        for (String date : dates) {
            List<String> colorList = new ArrayList<>();
            List<TodoDTO> todoDTOList = new ArrayList<>();
            for (Todo t : todoList) {
                Date startDate = t.getStartDate();
                Date endDate = t.getEndDate();
                if (todoUtil.isCurrentDateValidByStartDateAndEndDate(startDate, endDate, Date.valueOf(date))) {
                    if (t.getRepeatTag() != null) { // 반복이면
                        // 현재 날짜가 todo의 반복 요일에 포함이 되는지 확인
                        if (!todoUtil.isTodoDayOfWeekIncludesCurrentDate(Date.valueOf(date), t)) {
                            continue;
                        }
                    }
                    Member member = t.getMember();
                    String color = member.getColor();
                    colorList.add(color);

                    int isDone = getIsDone(t, Date.valueOf(date));
                    TodoDTO todoDto = TodoDTO.builder()
                            .todoId(t.getId())
                            .title(t.getTitle())
                            .memo(t.getMemo())
                            .timeTag(t.getTimeTag())
                            .repeatTag(t.getRepeatTag())
                            .color(member.getColor())
                            .memberId(member.getId())
                            .isDone(isDone)
                            .groupTodoId(t.getGroupTodoId())
                            .build();
                    todoDTOList.add(todoDto);
                }
            }
            // 중복 제거
            Set<String> set = new HashSet<>(colorList);
            List<String> newList = new ArrayList<>(set);

            colorListByDate.put(date, newList);
            todoListByDate.put(date, todoDTOList);
        }

        TreeMap<String, List<String>> colorListByDateSorted = new TreeMap<>(colorListByDate);
        TreeMap<String, List<TodoDTO>> todoListByDateSorted = new TreeMap<>(todoListByDate);
        return TodoResponse.builder().color(colorListByDateSorted).items(todoListByDateSorted).build();
    }

    public int getIsDone(Todo todo, Date date) {
        List<TodoDone> todoDoneList = todoDoneRepository.findByTodo(todo);
        if (todoDoneList.size() == 0) {
            return 0;
        }
        List<TodoDone> todoDoneFilterList = todoDoneList.stream().filter(t -> t.getDate().equals(date)).collect(Collectors.toList());
        if (todoDoneFilterList.size() == 0) {
            return 0;
        }
        TodoDone todoDone = todoDoneFilterList.get(0);
        return todoDone.getIsDone();
    }
}