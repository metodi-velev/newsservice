package com.example.newsservice.mappers;

import com.example.newsservice.dto.NewsDetailsDto;
import com.example.newsservice.entity.News;
import com.example.newsservice.entity.Photo;
import com.example.newsservice.entity.ReadStatus;
import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Mapper
public interface NewsMapper {
    News newsDetailsDtoToNews(NewsDetailsDto newsDetailsDto);

    NewsDetailsDto newsToNewsDetailsDto(News news);

    @Condition
    default boolean isNotEmpty(String value) {
        return StringUtils.hasText(value);
    }

    @Condition
    default boolean isNotNull(OffsetDateTime value) {
        return Objects.nonNull(value);
    }

    @Condition
    default boolean isNotNull(UUID value) {
        return Objects.nonNull(value);
    }

    @Condition
    default boolean isNotNull(Photo value) {
        return Objects.nonNull(value);
    }

    @Condition
    default boolean isNotNull(ReadStatus value) {
        return Objects.nonNull(value);
    }
}
