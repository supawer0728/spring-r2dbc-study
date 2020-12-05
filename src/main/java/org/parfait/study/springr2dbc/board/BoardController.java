package org.parfait.study.springr2dbc.board;

import java.util.Map;

import org.parfait.study.springr2dbc.board.dao.BoardDao;
import org.parfait.study.springr2dbc.board.domain.Board;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardDao boardDao;

    @GetMapping
    public Flux<Board> boards() {
        return boardDao.findAll().doOnNext(board -> log.info("board: {}", board));
    }

    @GetMapping("/{id}")
    public Mono<Board> board(@PathVariable long id) {
        return boardDao.findById(id);
    }

    @GetMapping("/{id}/writer/nickname")
    public Mono<Map<String, String>> writerNickname(@PathVariable long id) {
        return boardDao.findWriterNicknameById(id)
                       .map(nickname -> Map.of("nickname", nickname));
    }
}
