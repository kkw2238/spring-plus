package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    // 추가한 코드 : 쓰기도 진행하지만 Readonly로 설정되어 있던 것을 쓰기도 가능하게 수정
    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    public Page<TodoResponse> getTodos(int page, int size, String weather, LocalDate beginTime , LocalDate endTime) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Todo> todos;

        // 추가된 코드 : 시작 일자가 종료 일자보다 뒤에 있는 경우 에러 반환
        if (beginTime.isAfter(endTime)) {
            throw new InvalidRequestException("종료 일자는 시작 일자보다 뒤에 있어야 합니다");
        }

        LocalDateTime findTimeAt = beginTime.atStartOfDay();
        LocalDateTime findTimeEnd = endTime.atTime(LocalTime.MAX);

        /* 추가된 코드 : 날씨가 존재하는 경우 기간내에 있는 모든 Todo중 날씨가 일치하는 Todo를 검색
            그 외의 경우 : 기간내에 있는 모든 Todo를 검색
         */
        if (weather != null) {
            todos = todoRepository.findByWeatherInPeriod(weather, findTimeAt, findTimeEnd, pageable);
        } else {
            todos = todoRepository.findTodoInPeriod(findTimeAt, findTimeEnd, pageable);
        }
        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }
}
