package org.parfait.study.springr2dbc.member.domain;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface MemberHistoryRepository extends ReactiveSortingRepository<MemberHistory, Long> {
}
