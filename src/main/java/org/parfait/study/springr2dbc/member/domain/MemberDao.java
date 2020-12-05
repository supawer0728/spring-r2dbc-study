package org.parfait.study.springr2dbc.member.domain;

import static org.springframework.data.relational.core.query.Query.empty;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
public class MemberDao {
    private final R2dbcEntityTemplate template;
    private final MemberRepository memberRepository;
    private final MemberHistoryRepository memberHistoryRepository;

    @Transactional
    public Mono<Member> save(Member member) {
        return template.insert(member)
                       .flatMap(saved -> memberHistoryRepository.save(MemberHistory.create(saved)).thenReturn(saved));
    }

    public Flux<Member> saveAll(List<Member> members) {
        return memberRepository.saveAll(members);
    }

    public Mono<Page<Member>> findAll(Pageable pageable) {
//        return Mono.zip(template.select(Member.class)
//                                .matching(empty().limit(pageable.getPageSize()).offset(pageable.getOffset()))
//                                .all()
//                                .collectList(),
//                        template.count(empty(), Member.class),
//                        (members, count) -> {
//                            if (count < 1) {
//                                return Page.empty(pageable);
//                            }
//                            return new PageImpl<>(members, pageable, count);
//                        });
        return template.count(empty(), Member.class)
                       .flatMap(count -> {
                           if (count < 1) {
                               return Mono.just(Page.empty(pageable));
                           }
                           return template.select(empty().limit(pageable.getPageSize()).offset(pageable.getOffset()), Member.class)
                                          .collectList()
                                          .map(members -> new PageImpl<>(members, pageable, count));
                       });
    }
}
