package org.parfait.study.springr2dbc.member;

import org.parfait.study.springr2dbc.board.dao.BoardDao;
import org.parfait.study.springr2dbc.board.domain.Board;
import org.parfait.study.springr2dbc.member.domain.Member;
import org.parfait.study.springr2dbc.member.domain.MemberDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberDao memberDao;
    private final BoardDao boardDao;

    @GetMapping
    public Mono<Page<Member>> members(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return memberDao.findAll(pageable);
    }

    @GetMapping("/{id}/boards")
    public Flux<Board> boardsOfMember(@PathVariable long id) {
        return boardDao.findByWriterId(id);
    }

    @PostMapping
    public Mono<Member> save(@RequestBody Member member) {
        return memberDao.save(member);
    }
}
