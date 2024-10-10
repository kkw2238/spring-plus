package org.example.expert.jpa;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TodoCustomRepository {
    Optional<Todo> findByIdWithUser(Long todoId);
}
