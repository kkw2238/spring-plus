package org.example.expert.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TodoCustomRepositoryImpl implements TodoCustomRepository {

    private final JPAQueryFactory queryFactory;

    public TodoCustomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /** 추가된 코드
     *     @Query("SELECT t FROM Todo t " +
     *             "LEFT JOIN t.user " +
     *             "WHERE t.id = :todoId") 를 QueryDSL로 옮겨적은 코드
     * @param todoId 조회할 todoID
     * @return Optional로 묶인 조회된 Todo정보
     */
    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;

        var query = queryFactory.selectFrom(todo)
                .leftJoin(todo.user).fetchJoin() // N + 1 방지를 위한 fetch형태의 Join
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.of(query);
    }
}
