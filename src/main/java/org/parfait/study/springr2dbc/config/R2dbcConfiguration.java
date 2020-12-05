package org.parfait.study.springr2dbc.config;

import java.util.List;

import org.parfait.study.springr2dbc.member.domain.Member;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.mapping.event.BeforeSaveCallback;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@EnableR2dbcAuditing
@EnableR2dbcRepositories
@Configuration
public class R2dbcConfiguration extends AbstractR2dbcConfiguration {
    private final ConnectionFactory connectionFactory;

    @Bean
    public ReactiveAuditorAware<Long> reactiveAuditorAware() {
        return () -> Mono.just(1L);
    }

    @Bean
    public BeforeSaveCallback<Member> beforeSaveMemberCallback() {
        return (entity, row, collection) -> {
            log.info("entity: {}, row: {}, collection: {}", entity, row, collection);
            return Mono.just(entity);
        };
    }

    @Override
    public ConnectionFactory connectionFactory() {
        return connectionFactory;
    }

    @Override
    protected List<Object> getCustomConverters() {
        return super.getCustomConverters();
    }
}
