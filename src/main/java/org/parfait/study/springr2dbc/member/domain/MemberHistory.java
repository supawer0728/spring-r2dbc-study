package org.parfait.study.springr2dbc.member.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Value;

@Value
public class MemberHistory {
    @Id
    Long id;
    Long memberId;
    String nickname;
    @CreatedDate
    LocalDateTime createdAt;
    @LastModifiedDate
    LocalDateTime updatedAt;

    public static MemberHistory create(Member member) {
        return new MemberHistory(null, member.getId(), member.getNickname(), member.getCreatedAt(), member.getUpdatedAt());
    }
}
