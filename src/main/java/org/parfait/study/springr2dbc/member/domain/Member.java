package org.parfait.study.springr2dbc.member.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Member {
    @Id
    Long id;
    String nickname;
    @CreatedDate
    LocalDateTime createdAt;
    @LastModifiedDate
    LocalDateTime updatedAt;

    @JsonCreator
    public Member(@JsonProperty("id") Long id,
                  @JsonProperty("nickname") String nickname,
                  @JsonProperty("createdAt") LocalDateTime createdAt,
                  @JsonProperty("updatedAt") LocalDateTime updatedAt) {
        this.id = id;
        this.nickname = nickname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
