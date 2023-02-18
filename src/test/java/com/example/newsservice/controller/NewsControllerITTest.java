package com.example.newsservice.controller;

import com.example.newsservice.dto.NewsDetailsDto;
import com.example.newsservice.entity.News;
import com.example.newsservice.mappers.NewsMapper;
import com.example.newsservice.repository.NewsRepository;
import com.example.newsservice.security.SecurityConfig;
import com.example.newsservice.validators.NewsDetailsDtoValidator;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BindingResultUtils;
import org.springframework.validation.DataBinder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(SecurityConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NewsControllerITTest {

    private News news;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    NewsController newsController;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsMapper newsMapper;

    protected MockMvc mockMvc;

    NewsDetailsDto newsDetailsDtoUpdate;

    BindingResult bindingResult;

    @BeforeAll
    void setBeforeAll() {
        newsDetailsDtoUpdate = NewsDetailsDto.builder()
                .title("UPDATED Title")
                .text("UPDATED Text to the test title with minimum 20 characters.")
                .build();

        DataBinder binder = new DataBinder(newsDetailsDtoUpdate, "newsDetailsDtoUpdate");
        Map<?, ?> newsDetailsDtoMap = binder.getBindingResult().getModel();
        bindingResult = BindingResultUtils.getBindingResult(newsDetailsDtoMap, "newsDetailsDtoUpdate");
    }

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
        ResponseEntity<Page<NewsDetailsDto>> news = newsController.getAllNews(PageRequest.of(0, 6, Sort.Direction.DESC, "title"));

        assertThat(Objects.requireNonNull(news.getBody()).getContent().size()).isEqualTo(3);
    }

    @Rollback
    @Transactional
    @Test
    @WithUserDetails("martin")
    void getAllNewsEmptyList() {
        newsRepository.deleteAll();
        ResponseEntity<Page<NewsDetailsDto>> news = newsController.getAllNews(PageRequest.of(0, 6, Sort.Direction.DESC, "title"));

        assertThat(Objects.requireNonNull(news.getBody()).getContent().size()).isEqualTo(0);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getNewsById() throws Exception {
        News news = newsRepository.findAll().get(0);

        mockMvc.perform(get("/news/" + news.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is(news.getTitle())))
                .andExpect(jsonPath("$.text", is(news.getText())))
                .andExpect(jsonPath("$.allowedRole", is(news.getAllowedRole())));
    }

    @Test
    @WithUserDetails("martin")
    void getNewsByIdWithRealUser() {
        News news = newsRepository.findAll().get(0);

        NewsDetailsDto dto = newsController.getNewsById(news.getId()).getBody();

        assertThat(dto).isNotNull();
        assertThat(dto.getTitle()).isEqualTo(news.getTitle());
        assertThat(dto.getText()).isEqualTo(news.getText());
        assertThat(dto.getValidTo()).isEqualTo(news.getValidTo());
        assertThat(dto.getReadStatus()).isEqualTo(news.getReadStatus());
        assertThat(dto.getPhoto()).isEqualTo(news.getPhoto());
    }

    @Test
    @WithUserDetails("martin")
    void getNewsByIdNotFound() {
        UUID newsId = UUID.randomUUID();
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found. News id: " + newsId);

        ResponseStatusException exceptionThrown = assertThrows(ResponseStatusException.class, () -> newsController.getNewsById(newsId));

        assertTrue(Objects.requireNonNull(exceptionThrown.getMessage()).contains("News not found. News id: " + newsId));
        assertThat(exceptionThrown.getMessage()).isEqualTo(exception.getMessage());
        assertThat(exceptionThrown.getStatus()).isEqualTo(exception.getStatus());
        assertThat(exceptionThrown.getReason()).isEqualTo(exception.getReason());
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

    @Rollback
    @Transactional
    @Test
    @WithUserDetails("lisa")
    void addNews() {
        ResponseEntity<NewsDetailsDto> responseEntity = newsController.addNews(newsDetailsDtoUpdate, bindingResult);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[2]);

        News news = newsRepository.findById(savedUUID).get();
        assertThat(news).isNotNull();
        assertThat(news.getTitle()).isEqualTo(newsDetailsDtoUpdate.getTitle());
        assertThat(news.getText()).isEqualTo(newsDetailsDtoUpdate.getText());
    }

    @Rollback
    @Transactional
    @Test
    @WithUserDetails("lisa")
    void deleteNews() {
        News news = newsRepository.findAll().get(0);

        ResponseEntity<NewsDetailsDto> responseEntity = newsController.deleteNews(news.getId());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(204));

        assertThat(newsRepository.findById(news.getId())).isEmpty();
    }

    @Test
    @WithUserDetails("lisa")
    void deleteNewsNotFound() {
        UUID newsId = UUID.randomUUID();
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found. News id: " + newsId);

        ResponseStatusException exceptionThrown = assertThrows(ResponseStatusException.class,
                () -> newsController.deleteNews(newsId));

        assertTrue(Objects.requireNonNull(exceptionThrown.getMessage()).contains("News not found. News id: " + newsId));
        assertThat(exceptionThrown.getMessage()).isEqualTo(exception.getMessage());
        assertThat(exceptionThrown.getStatus()).isEqualTo(exception.getStatus());
        assertThat(exceptionThrown.getReason()).isEqualTo(exception.getReason());
    }

    @Rollback
    @Transactional
    @Test
    @WithUserDetails("lisa")
    void updateNews() {
        News news = newsRepository.findAll().get(0);
        NewsDetailsDto newsDetailsDto = newsMapper.newsToNewsDetailsDto(news);

        ResponseEntity<NewsDetailsDto> response = newsController.updateNews(newsDetailsDto.getId(), newsDetailsDtoUpdate, bindingResult);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.valueOf(200));
        assertThat(response.getBody().getTitle()).isEqualTo(newsDetailsDtoUpdate.getTitle());
        assertThat(response.getBody().getText()).isEqualTo(newsDetailsDtoUpdate.getText());

        News updatedNews = newsRepository.findById(news.getId()).get();
        assertThat(updatedNews).isNotNull();
        assertThat(updatedNews.getTitle()).isEqualTo(newsDetailsDtoUpdate.getTitle());
        assertThat(updatedNews.getText()).isEqualTo(newsDetailsDtoUpdate.getText());
    }

    @Test
    @WithUserDetails("lisa")
    void updateNewsNotFound() {
        UUID newsId = UUID.randomUUID();
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found. News id: " + newsId);

        ResponseStatusException exceptionThrown = assertThrows(ResponseStatusException.class,
                () -> newsController.updateNews(newsId, newsDetailsDtoUpdate, bindingResult));

        assertTrue(Objects.requireNonNull(exceptionThrown.getMessage()).contains("News not found. News id: " + newsId));
        assertThat(exceptionThrown.getMessage()).isEqualTo(exception.getMessage());
        assertThat(exceptionThrown.getStatus()).isEqualTo(exception.getStatus());
        assertThat(exceptionThrown.getReason()).isEqualTo(exception.getReason());
    }

    @Test
    @WithUserDetails("lisa")
    void updateNewsNotValid() {
        News news = newsRepository.findAll().get(0);
        NewsDetailsDto newsDetailsDto = newsMapper.newsToNewsDetailsDto(news);

        NewsDetailsDto newsDetailsDtoUpdateNotValid = NewsDetailsDto.builder()
                .title("UP")  //Title is not valid - it is shorter than 6 chars.
                .text("UPDATED Text") //Text is not valid - it is shorter than 20 chars.
                .build();

        DataBinder dataBinder = new DataBinder(newsDetailsDtoUpdateNotValid);
        dataBinder.addValidators(new NewsDetailsDtoValidator());
        dataBinder.validate();

        ResponseStatusException exception = new ResponseStatusException(HttpStatus.BAD_REQUEST, "The following news fields are not valid: title, text");

        ResponseStatusException exceptionThrown = assertThrows(ResponseStatusException.class,
                () -> newsController.updateNews(newsDetailsDto.getId(), newsDetailsDtoUpdateNotValid, dataBinder.getBindingResult()));

        assertTrue(Objects.requireNonNull(exceptionThrown.getMessage()).contains("The following news fields are not valid: title, text"));
        assertThat(exceptionThrown.getMessage()).isEqualTo(exception.getMessage());
        assertThat(exceptionThrown.getStatus()).isEqualTo(exception.getStatus());
        assertThat(exceptionThrown.getReason()).isEqualTo(exception.getReason());
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