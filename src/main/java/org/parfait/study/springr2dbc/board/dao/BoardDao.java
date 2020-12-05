package org.parfait.study.springr2dbc.board.dao;

import static org.springframework.data.domain.Sort.Order.desc;
import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.util.Objects;

import org.parfait.study.springr2dbc.board.domain.Board;
import org.parfait.study.springr2dbc.board.domain.BoardRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
public class BoardDao {
    private final BoardRepository boardRepository;
    private final R2dbcEntityTemplate template;

    public Mono<Board> findById(long id) {
        return template.select(Board.class)
                       .matching(query(where("id").is(id)))
                       .one();
    }

    public Flux<Board> findByWriterId(long writerId) {
        return template.select(Board.class)
                       .matching(query(where("writerId").is(writerId)).sort(by(desc("createdAt"))))
                       .all();
    }

    public Mono<String> findWriterNicknameById(long id) {
        return template.getDatabaseClient()
                       .sql("SELECT m.nickname FROM board b JOIN member m ON b.writer_id = m.id WHERE b.id = :id")
                       .bind("id", id)
                       .fetch()
                       .one()
                       .map(result -> Objects.toString(result.get("nickname")));
    }

    public Flux<Board> findAll() {
        return boardRepository.findAll();
    }

    public Mono<Integer> blockByWriterId(long writerId) {
        return template.update(Board.class)
                       .matching(query(where("writerId").is(writerId)))
                       .apply(Update.update("title", "Blocked Board"));

    }
}
