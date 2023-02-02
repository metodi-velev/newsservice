package com.example.newsservice.controller;

import com.example.newsservice.dto.NewsDto;
import com.example.newsservice.dto.ReadStatusDto;
import com.example.newsservice.entity.News;
import com.example.newsservice.security.User;
import com.example.newsservice.service.NewsService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/news")
@Api(description = "APIs for news service")
public class NewsController {

    private static final Logger LOG = LoggerFactory.getLogger(NewsController.class);

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping(value = "/ping", produces = "application/json")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok().body(newsService.ping());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(produces = "application/json")
    public ResponseEntity<Page<News>> getAllNews(Pageable pageable) {
        LOG.info("Fetching all news, page : {} page size : {} ", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok().body(newsService.getAllNews(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "{id}", produces = "application/json")
    public ResponseEntity<News> getNewsById(@PathVariable(value = "id") UUID newsId) {
        Optional<News> news = newsService.getNewsById(newsId);
        LOG.info("Fetching news by Id : {}", newsId);
        if (!news.isPresent()) {
            LOG.info("News not found for Id : {}", newsId);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(news.get());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READER', 'PUBLISHER')")
    @GetMapping(value = "/accountId/{accountId}/roleName/{role}", produces = "application/json")
    public ResponseEntity<List<News>> getNewsForSingleAccountAndRole(@PathVariable(value = "accountId") Integer accountId,
                                                                     @PathVariable(value = "role") String role) {
        ResponseEntity<List<News>> build = checkUserExists(accountId);
        if (build != null) return build;
        return ResponseEntity.ok().body(newsService.getRecentNewsForSingleAccountAndRole(role, accountId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READER', 'PUBLISHER')")
    @GetMapping(value = "/accountId/{accountId}", produces = "application/json")
    public ResponseEntity<List<News>> getNewsForSingleAccount(@PathVariable(value = "accountId") Integer accountId
    ) {
        ResponseEntity<List<News>> build = checkUserExists(accountId);
        if (build != null) return build;
        return ResponseEntity.ok().body(newsService.getRecentNewsForSingleAccount(accountId));
    }

    @PreAuthorize("hasRole('READER')")
    @GetMapping(value = "titles", produces = "application/json")
    public ResponseEntity<List<NewsDto>> readAllTitles() {
        List<NewsDto> titles = newsService.readAllTitles();
        LOG.info("Fetching all titles");
        return ResponseEntity.ok().body(titles);
    }

    @PreAuthorize("hasRole('PUBLISHER')")
    @PostMapping(produces = "application/json")
    public ResponseEntity<News> addNews(@Valid @RequestBody News news) {
        News addedNews = newsService.addNews(news);
        LOG.info("Created News with Id: {} and title : {}", addedNews.getId(), addedNews.getTitle());
        return new ResponseEntity<>(addedNews, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER')")
    @DeleteMapping(value = "{id}", produces = "application/json")
    public ResponseEntity<News> deleteNews(@PathVariable(value = "id") UUID newsId) {
        Optional<News> news = newsService.getNewsById(newsId);
        if (!news.isPresent()) {
            LOG.info("News not found for Id : {}", newsId);
            return ResponseEntity.notFound().build();
        }
        LOG.info("Deleted News with Id : {}", newsId);
        newsService.deleteNews(news.get());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER')")
    @PutMapping("{id}")
    public ResponseEntity<News> updateNews(@PathVariable(value = "id") UUID newsId,
                                           @Valid @RequestBody News newsDetails) throws InvocationTargetException, IllegalAccessException {
        Optional<News> news = newsService.getNewsById(newsId);
        if (!news.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        news.get().setId(newsId);
        LOG.info("Updating News for Id : {}", newsId);
        News updatedNews = newsService.addNews(newsDetails);
        return new ResponseEntity<>(updatedNews, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READER', 'PUBLISHER')")
    @PutMapping(path = {"{newsId}/{accountId}"}, produces = {"application/json"})
    public ResponseEntity<String> updateNewsReadStatus(@PathVariable("newsId") UUID newsId,
                                                       @PathVariable("accountId") Integer accountId,
                                                       @Valid @RequestBody ReadStatusDto readStatusDto) {
        newsService.updateNewsReadStatus(newsId, accountId, readStatusDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<List> badReqeustHandler(ConstraintViolationException e) {
        List<String> errors = new ArrayList<>(e.getConstraintViolations().size());

        e.getConstraintViolations().forEach(constraintViolation -> {
            errors.add(constraintViolation.getPropertyPath().toString() + " : " + constraintViolation.getMessage());
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<List<News>> checkUserExists(Integer accountId) {
        Optional<User> user = newsService.findUserById(accountId);
        LOG.info("Fetching user by Id : {}", accountId);
        if (!user.isPresent()) {
            LOG.info("User not found for Id : {}", accountId);
            return ResponseEntity.notFound().build();
        }
        return null;
    }
}
