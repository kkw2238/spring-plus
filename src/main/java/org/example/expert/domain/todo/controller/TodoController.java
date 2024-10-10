package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.Protocol;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todos")
    public ResponseEntity<TodoSaveResponse> saveTodo(
            @Auth AuthUser authUser,
            @Valid @RequestBody TodoSaveRequest todoSaveRequest
    ) {
        return ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequest));
    }

    /** 수정된 코드
     * todo조회시 사용할 조건 추가
     * @param weather : 날씨 정보
     * @param beginTime : yyyy-MM-dd 형태의 기간 시작 일자
     * @param endTime : yyyy-MM-dd 형태의 기간 끝 일자
     */
    @GetMapping("/todos")
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String weather,
            @RequestParam(required = false, defaultValue = Protocol.DEFAULT_BEGIN_VALUE) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beginTime,
            @RequestParam(required = false, defaultValue = Protocol.DEFAULT_END_VALUE) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endTime
            ) {
        return ResponseEntity.ok(todoService.getTodos(page, size, weather, beginTime, endTime));
    }

    @GetMapping("/todos/{todoId}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable long todoId) {
        return ResponseEntity.ok(todoService.getTodo(todoId));
    }

    /** 추가된 코드 : DateTime 포멧으로 들어오는 경우, 해당 바인더를 통해 데이터를 저장
     * BEGIN_TIME이 들어올 경우, 0000-01-01 반환
     * END_TIME이 들어올 경우, 현재 시간 반환
     * 그 외의 경우, text그대로 반환
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        final DateFormat dateFormat = new SimpleDateFormat(Protocol.FORMAT);
        final CustomDateEditor dateEditor = new CustomDateEditor(dateFormat, true) {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text.equals(Protocol.DEFAULT_END_VALUE)) {
                    setValue(LocalDate.now());
                } else if(text.equals(Protocol.DEFAULT_BEGIN_VALUE)) {
                    setValue(LocalDate.of(0, 1, 1));
                } else {
                    super.setAsText(text);
                }
            }
        };

        binder.registerCustomEditor(LocalDate.class, dateEditor);
    }
}
