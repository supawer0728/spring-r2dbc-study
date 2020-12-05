package org.parfait.study.springr2dbc.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Configuration
public class JsonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.mixIn(PageImpl.class, PageImplMixIn.class)
                                 .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }

    public static abstract class PageImplMixIn<T> extends PageImpl<T> {
        public PageImplMixIn(List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }

        public PageImplMixIn(List<T> content) {
            super(content);
        }

        @JsonIgnore
        @Override
        public Pageable getPageable() {
            return super.getPageable();
        }

        @JsonIgnore
        @Override
        public Sort getSort() {
            return super.getSort();
        }
    }
}
