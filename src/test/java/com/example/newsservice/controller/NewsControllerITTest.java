package com.example.newsservice.controller;

import com.example.newsservice.entity.News;
import com.example.newsservice.repository.NewsRepository;
import com.example.newsservice.security.SecurityConfig;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(SecurityConfig.class)
class NewsControllerITTest {

    private News news;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    NewsController newsController;

    @Autowired
    private NewsRepository newsRepository;

    protected MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();

        news = new News();
        news.setId(UUID.randomUUID());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @WithUserDetails("martin")
    void getAllNews() {
        ResponseEntity<Page<News>> news = newsController.getAllNews(PageRequest.of(0, 6, Sort.Direction.DESC, "title"));

        assertThat(Objects.requireNonNull(news.getBody()).getContent().size()).isEqualTo(3);
    }

    @Rollback
    @Transactional
    @Test
    @WithUserDetails("martin")
    void getAllNewsEmptyList() {
        newsRepository.deleteAll();
        ResponseEntity<Page<News>> news = newsController.getAllNews(PageRequest.of(0, 6, Sort.Direction.DESC, "title"));

        assertThat(Objects.requireNonNull(news.getBody()).getContent().size()).isEqualTo(0);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getNewsById() throws Exception {
        News news = newsRepository.findAll().get(0);

        mockMvc.perform(get("/news/" + news.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @DisplayName("Get News By ID")
    @Nested
    class GetNewsById {

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("com.example.newsservice.controller.NewsControllerITTest#getStreamAllUsers")
        void getNewsById(String user, String pwd) throws Exception {
            News news = newsRepository.findAll().get(0);

            mockMvc.perform(get("/news/" + news.getId())
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        void getNewsByIdUnauthenticated() throws Exception {
            News news = newsRepository.findAll().get(0);

            mockMvc.perform(get("/news/" + news.getId()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void getNewsForSingleAccountAndRole() {
    }

    @Test
    void getNewsForSingleAccount() {
    }

    @Test
    void getPictureForNewsIdAndPictureIdAndRole() {
    }

    @Test
    void readAllTitles() {
    }

    @Test
    void addNews() {
    }

    @Test
    void deleteNews() {
    }

    @Test
    void updateNews() {
    }

    @Test
    void updateNewsReadStatus() {
    }

    @Test
    void badReqeustHandler() {
    }

    public static Stream<Arguments> getStreamAllUsers() {
        return Stream.of(Arguments.of("martin", "martin"));
    }
}