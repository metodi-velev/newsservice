package com.example.newsservice.controller;

import com.example.newsservice.SecurityTestConfig;
import com.example.newsservice.dto.NewsDetailsDto;
import com.example.newsservice.entity.News;
import com.example.newsservice.mappers.NewsMapper;
import com.example.newsservice.mappers.NewsMapperImpl;
import com.example.newsservice.service.NewsService;
import com.example.newsservice.utils.OffsetDateTimeDeserializer;
import com.example.newsservice.utils.OffsetDateTimeSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({NewsController.class})
@Import({SecurityTestConfig.class, NewsMapperImpl.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NewsControllerUnitTest {

    private News news1;
    private News news2;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    NewsMapper newsMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    NewsService newsService;

    @BeforeAll
    void setBeforeAll() {
        final SimpleModule offsetDateTimeSerialization = new SimpleModule();
        offsetDateTimeSerialization.addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer());
        offsetDateTimeSerialization.addDeserializer(OffsetDateTime.class, new OffsetDateTimeDeserializer());

        objectMapper.registerModule(offsetDateTimeSerialization);
    }

    @BeforeEach
    void setUp() {
        news1 = News.builder()
                .id(UUID.randomUUID())
                .title("Unit test title 1")
                .text("Sample text to the title 1")
                .validFrom(OffsetDateTime.parse("2020-03-29T02:05:25+02:00"))
                .validTo(OffsetDateTime.parse("2020-06-29T02:05:25+02:00"))
                .allowedRole("READER")
                .build();

        news2 = News.builder()
                .id(UUID.randomUUID())
                .title("Unit test title 2")
                .text("Sample text to the title 2")
                .validFrom(OffsetDateTime.parse("2003-06-29T02:05:25+02:00"))
                .validTo(OffsetDateTime.parse("2003-09-29T02:05:25+02:00"))
                .allowedRole("READER")
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void ping() {
    }

    @Test
    @WithUserDetails("martin")
    void getAllNews() throws Exception {
        given(newsService.getAllNews(any(Pageable.class))).willReturn(new PageImpl<>(Arrays.asList(news1, news2)));

        mockMvc.perform(get("/news")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(2)))
                .andExpect(jsonPath("$.content[0].title", is(news1.getTitle())));
    }

    @Test
    @WithAnonymousUser
    void getAllNewsUnauthenticated() throws Exception {
        mockMvc.perform(get("/news")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(newsService, never()).getAllNews(any(Pageable.class));
        verifyNoInteractions(newsService);
    }

    @Test
    @WithUserDetails("lisa")
    void getAllNewsForbidden() throws Exception {
        mockMvc.perform(get("/news")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(newsService, never()).getAllNews(any(Pageable.class));
        verifyNoInteractions(newsService);
    }

    @Test
    @WithUserDetails("martin")
    void getNewsById() throws Exception {
        given(newsService.getNewsById(any(UUID.class))).willReturn(news1);

        mockMvc.perform(get("/news/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(
                        "  {\n" +
                                "    \"title\": \"Unit test title 1\",\n" +
                                "    \"text\": \"Sample text to the title 1\",\n" +
                                "    \"validFrom\": \"2020-03-29T02:05:25+02:00\",\n" +
                                "    \"validTo\": \"2020-06-29T02:05:25+02:00\",\n" +
                                "    \"allowedRole\": \"READER\" \n" +
                                "  }\n"
                ))
                .andExpect(jsonPath("$.title", is(news1.getTitle())))
                .andExpect(jsonPath("$.text", is(news1.getText())))
                .andExpect(jsonPath("$.allowedRole", is(news1.getAllowedRole())));
    }

    @Test
    @WithUserDetails("martin")
    void getNewsByIdNotFound() throws Exception {
        UUID newsId = UUID.randomUUID();
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found. News id: " + newsId);

        given(newsService.getNewsById(any(UUID.class))).willThrow(exception);

        mockMvc.perform(get("/news/{newsId}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("404 NOT_FOUND \"News not found. News id: " + newsId + "\"", result.getResolvedException().getMessage()));
    }

    @Test
    @WithAnonymousUser
    void getNewsByIdUnauthenticated() throws Exception {
        mockMvc.perform(get("/news/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(newsService, never()).getNewsById(any(UUID.class));
        verifyNoInteractions(newsService);
    }

    @Test
    @WithUserDetails("lisa")
    void getNewsByIdForbidden() throws Exception {
        mockMvc.perform(get("/news/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(newsService, never()).getNewsById(any(UUID.class));
        verifyNoInteractions(newsService);
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
    @WithUserDetails("lisa")
    void addNews() throws Exception {
        news1.setId(null);

        given(newsService.addNews(any(News.class))).willReturn(news2);

        mockMvc.perform(post("/news")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(news1)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/news/" + news2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(news2.getId().toString()))
                .andExpect(jsonPath("$.title").value(news2.getTitle()))
                .andExpect(jsonPath("$.text").value(news2.getText()))
                .andExpect(jsonPath("$.validFrom").value(news2.getValidFrom().toString()))
                .andExpect(jsonPath("$.validTo").value(news2.getValidTo().toString()))
                .andExpect(jsonPath("$.allowedRole").value(news2.getAllowedRole()));
    }

    @Test
    @WithAnonymousUser
    void addNewsUnauthenticated() throws Exception {
        news1.setId(null);

        given(newsService.addNews(any(News.class))).willReturn(news2);

        mockMvc.perform(post("/news")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(news1)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(newsService, never()).addNews(any(News.class));
        verifyNoInteractions(newsService);
    }

    @Test
    @WithUserDetails("martin")
    void addNewsForbidden() throws Exception {
        news1.setId(null);

        given(newsService.addNews(any(News.class))).willReturn(news2);

        mockMvc.perform(post("/news")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(news1)))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(newsService, never()).addNews(any(News.class));
        verifyNoInteractions(newsService);
    }

    @Test
    @WithUserDetails("lisa")
    void deleteNews() throws Exception {
        mockMvc.perform(delete("/news/" + news1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        ArgumentCaptor<UUID> uuidArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(newsService, times(1)).deleteNews(uuidArgumentCaptor.capture());

        assertThat(news1.getId()).isEqualTo(uuidArgumentCaptor.getValue());
    }

    @Test
    @WithUserDetails("martin")
    void deleteNewsNotFound() throws Exception {
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found. News id: " + news1.getId());

        doThrow(exception).when(newsService).deleteNews(news1.getId());

        mockMvc.perform(delete("/news/{newsId}", news1.getId()))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("404 NOT_FOUND \"News not found. News id: " + news1.getId() + "\"", result.getResolvedException().getMessage()));
    }

    @Test
    @WithAnonymousUser
    void deleteNewsUnauthenticated() throws Exception {
        mockMvc.perform(delete("/news/" + news1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(newsService, never()).deleteNews(any(UUID.class));
        verifyNoInteractions(newsService);
    }

    @Test
    @WithUserDetails("john")
    void deleteNewsForbidden() throws Exception {
        mockMvc.perform(delete("/news/" + news1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(newsService, never()).deleteNews(any(UUID.class));
        verifyNoInteractions(newsService);
    }

    @Test
    @WithUserDetails("lisa")
    void updateNews() throws Exception {
        given(newsService.updateNews(any(UUID.class), any(NewsDetailsDto.class), any(BindingResult.class))).willReturn(news2);

        mockMvc.perform(put("/news/" + news1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newsMapper.newsToNewsDetailsDto(news1))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(news2.getId().toString()))
                .andExpect(jsonPath("$.title").value(news2.getTitle()))
                .andExpect(jsonPath("$.text").value(news2.getText()))
                .andExpect(jsonPath("$.validFrom").value(news2.getValidFrom().toString()))
                .andExpect(jsonPath("$.validTo").value(news2.getValidTo().toString()))
                .andExpect(jsonPath("$.allowedRole").value(news2.getAllowedRole()));

        verify(newsService, times(1)).updateNews(any(UUID.class), any(NewsDetailsDto.class), any(BindingResult.class));
    }

    @Test
    @WithUserDetails("lisa")
    void updateNewsNotFound() throws Exception {
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found. News id: " + news1.getId());

        Map<String, Object> newsMap = new HashMap<>();
        newsMap.put("title", "New Title");

        given(newsService.updateNews(any(UUID.class), any(NewsDetailsDto.class), any(BindingResult.class))).willThrow(exception);

        mockMvc.perform(put("/news/{newsId}", news1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newsMap)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("404 NOT_FOUND \"News not found. News id: " + news1.getId() + "\"", result.getResolvedException().getMessage()));

        verify(newsService, times(1)).updateNews(any(UUID.class), any(NewsDetailsDto.class), any(BindingResult.class));
    }

    @Test
    @WithUserDetails("lisa")
    void updateNewsNotValid() throws Exception {
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.BAD_REQUEST, "The following news fields are not valid: title, text");

        NewsDetailsDto newsDetailsDto = new NewsDetailsDto();
        newsDetailsDto.setTitle("Short");
        newsDetailsDto.setText("Short");

        given(newsService.updateNews(any(UUID.class), any(NewsDetailsDto.class), any(BindingResult.class))).willThrow(exception);

        mockMvc.perform(put("/news/{newsId}", news1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newsDetailsDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The following news fields are not valid: title, text\"", result.getResolvedException().getMessage()));

        verify(newsService, times(1)).updateNews(any(UUID.class), any(NewsDetailsDto.class), any(BindingResult.class));
    }

    @Test
    @WithAnonymousUser
    void updateNewsUnauthenticated() throws Exception {
        mockMvc.perform(put("/news/" + news1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newsMapper.newsToNewsDetailsDto(news1))))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(newsService, never()).updateNews(any(UUID.class), any(NewsDetailsDto.class), any(BindingResult.class));
        verifyNoInteractions(newsService);
    }

    @Test
    @WithUserDetails("john")
    void updateNewsForbidden() throws Exception {
        mockMvc.perform(put("/news/" + news1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newsMapper.newsToNewsDetailsDto(news1))))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(newsService, never()).updateNews(any(UUID.class), any(NewsDetailsDto.class), any(BindingResult.class));
        verifyNoInteractions(newsService);
    }

    @Test
    void updateNewsReadStatus() {
    }

    @Test
    void badReqeustHandler() {
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}