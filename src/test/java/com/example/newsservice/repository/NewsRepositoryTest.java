package com.example.newsservice.repository;

import com.example.newsservice.entity.News;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void testSaveNewsTitleTooLong() {

        assertThrows(ConstraintViolationException.class, () -> {
            News savedNews = newsRepository.save(News.builder()
                    .title("Test Title which is longer than 50 characters and thus is rejected.")
                    .text("Test Text")
                    .build());

            newsRepository.flush();

            assertThat(savedNews).isNull();
        });
    }

    @Test
    void testSaveNewsLinkToPhotoNotUnique() {
        News savedNews1 = newsRepository.save(News.builder()
                .title("Test Title")
                .text("Test Text")
                .linkToPhoto("unique-link")
                .build());

        assertThat(savedNews1).isNotNull();
        assertThat(savedNews1.getId()).isNotNull();
        assertThat(savedNews1.getTitle()).isEqualTo("Test Title");
        assertThat(savedNews1.getText()).isEqualTo("Test Text");
        assertThat(savedNews1.getLinkToPhoto()).isEqualTo("unique-link");

        assertThrows(DataIntegrityViolationException.class, () -> {
            News savedNews2 = newsRepository.save(News.builder()
                    .title("Test Title")
                    .text("Test Text")
                    .linkToPhoto("unique-link")
                    .build());

            newsRepository.flush();

            assertThat(savedNews2).isNull();
        });
    }
}