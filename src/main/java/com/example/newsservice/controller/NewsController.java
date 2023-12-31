package com.example.newsservice.controller;

import com.example.newsservice.dto.NewsDetailsDto;
import com.example.newsservice.dto.NewsDto;
import com.example.newsservice.dto.ReadStatusDto;
import com.example.newsservice.entity.News;
import com.example.newsservice.entity.Photo;
import com.example.newsservice.exception.NotFoundException;
import com.example.newsservice.security.User;
import com.example.newsservice.service.NewsService;
import com.example.newsservice.utils.BasicInfo;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping("/news")
@Api("APIs for news service")
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
    public ResponseEntity<Page<NewsDetailsDto>> getAllNews(Pageable pageable) {
        LOG.info("Fetching all news, page : {} page size : {} ", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok().body(newsService.getAllNews(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "{id}", produces = "application/json")
    public ResponseEntity<NewsDetailsDto> getNewsById(@PathVariable(value = "id") UUID newsId) {
        NewsDetailsDto news = newsService.getNewsById(newsId);
        LOG.info("Fetching news by Id : {}", newsId);
        return ResponseEntity.ok().body(news);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READER', 'PUBLISHER')")
    @GetMapping(value = "/account/{accountId}/role/{role}", produces = "application/json")
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

    @PreAuthorize("hasAnyRole('ADMIN', 'READER', 'PUBLISHER')")
    @GetMapping(value = "/{newsId}/picture/{pictureId}/role/{role}", produces = IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPictureForNewsIdAndPictureIdAndRole(@PathVariable(value = "newsId") UUID newsId,
                                                                         @PathVariable(value = "pictureId") UUID pictureId,
                                                                         @PathVariable(value = "role") String role) throws IOException {
        Photo photo = newsService.getPictureForNewsIdAndPictureIdAndRole(newsId, pictureId, role);
        URL url = new URL(photo.getLinkToPhoto());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return ResponseEntity.ok().body(byteArrayOutputStream.toByteArray());
    }

    @PreAuthorize("hasRole('READER')")
    @GetMapping(value = "titles", produces = "application/json")
    public ResponseEntity<List<NewsDto>> readAllTitles() {
        List<NewsDto> titles = newsService.readAllTitles();
        LOG.info("Fetching all titles");
        return ResponseEntity.ok().body(titles);
    }

    @PreAuthorize("hasRole('PUBLISHER')")
    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<NewsDetailsDto> addNews(@Validated(BasicInfo.class) @RequestBody NewsDetailsDto news, BindingResult bindingResult) {
        NewsDetailsDto addedNews = newsService.addNews(news, bindingResult);
        LOG.info("Created News with Id: {} and title : {}", addedNews.getId(), addedNews.getTitle());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/news/" + addedNews.getId().toString());

        return new ResponseEntity<>(addedNews, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER')")
    @DeleteMapping(value = "{id}")
    public ResponseEntity<NewsDetailsDto> deleteNews(@PathVariable(value = "id") UUID newsId) {
        newsService.deleteNews(newsId);
        LOG.info("Deleted News with Id : {}", newsId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER')")
    @PutMapping(value = "{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<NewsDetailsDto> updateNews(@PathVariable(value = "id") UUID newsId,
                                                     @Validated(BasicInfo.class) @RequestBody NewsDetailsDto newsDetailsDto, BindingResult bindingResult) {
        NewsDetailsDto updatedNews = newsService.updateNews(newsId, newsDetailsDto, bindingResult);
        return new ResponseEntity<>(updatedNews, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READER', 'PUBLISHER')")
    @PutMapping(path = {"{newsId}/account/{accountId}"}, produces = {"application/json"})
    public ResponseEntity<String> updateNewsReadStatus(@PathVariable("newsId") UUID newsId,
                                                       @PathVariable("accountId") Integer accountId,
                                                       @Valid @RequestBody ReadStatusDto readStatusDto) {
        newsService.updateNewsReadStatus(newsId, accountId, readStatusDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER')")
    @PatchMapping(value = "{newsId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<NewsDetailsDto> updateBeerPatchById(@PathVariable("newsId") UUID newsId,
                                                              @RequestBody NewsDetailsDto news) { //omit Validated to test JPA validation on entity level

        NewsDetailsDto updatedNews = newsService.patchNewsById(newsId, news).orElseThrow(NotFoundException::new);

        return new ResponseEntity<>(updatedNews, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<List<String>> badRequestHandler(ConstraintViolationException e) {
        List<String> errors = new ArrayList<>(e.getConstraintViolations().size());

        e.getConstraintViolations().forEach(constraintViolation -> errors.add(constraintViolation.getPropertyPath().toString() + " : " + constraintViolation.getMessage()));

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
