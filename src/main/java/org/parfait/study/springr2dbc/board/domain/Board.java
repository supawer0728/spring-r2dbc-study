package org.parfait.study.springr2dbc.board.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.Value;

@Value
public class Board {
    @Id
    Long id;
    String title;
    String content;
    Long writerId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
