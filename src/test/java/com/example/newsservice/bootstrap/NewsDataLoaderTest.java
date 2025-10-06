package com.example.newsservice.bootstrap;

import com.example.newsservice.repository.NewsRepository;
import com.example.newsservice.repository.PhotoRepository;
import com.example.newsservice.repository.ReadStatusRepository;
import com.example.newsservice.repository.UserRepository;
import com.example.newsservice.service.PhotoService;
import com.example.newsservice.service.PhotoServiceImpl;
import com.example.newsservice.utils.PhotoUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({PhotoUtils.class, PhotoServiceImpl.class})
class NewsDataLoaderTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private PhotoService photoService;

    private NewsDataLoader newsDataLoader;

    @BeforeEach
    void setUp() {
        newsDataLoader = new NewsDataLoader(newsRepository, readStatusRepository, userRepository, photoRepository, photoService);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void run() throws Exception {
        newsDataLoader.run("");

        assertThat(newsRepository.count()).isEqualTo(3);
        assertThat(readStatusRepository.count()).isEqualTo(3);
        assertThat(photoRepository.count()).isEqualTo(3);
    }

    @Test
    void loadNewsData() {
    }
}