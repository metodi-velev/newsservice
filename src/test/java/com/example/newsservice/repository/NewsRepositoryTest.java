package com.example.newsservice.repository;

import com.example.newsservice.entity.News;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NewsRepositoryTest {

    @Autowired
    NewsRepository newsRepository;

    @Test
    void testSaveNews() {
        News savedNews = newsRepository.save(News.builder()
                .title("Test Title")
                .text("Test Text")
                .build());

        assertThat(savedNews).isNotNull();
        assertThat(savedNews.getId()).isNotNull();
        assertThat(savedNews.getTitle()).isEqualTo("Test Title");
        assertThat(savedNews.getText()).isEqualTo("Test Text");
    }

}