package com.example.newsservice.service;

import com.example.newsservice.dto.NewsDetailsDto;
import com.example.newsservice.dto.NewsDto;
import com.example.newsservice.dto.ReadStatusDto;
import com.example.newsservice.entity.News;
import com.example.newsservice.entity.Photo;
import com.example.newsservice.entity.ReadStatus;
import com.example.newsservice.repository.*;
import com.example.newsservice.security.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Optionals;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NewsServiceImpl implements NewsService {

    public static final String ROLE_PREFIX = "ROLE_";
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final PhotoRepository photoRepository;
    private final RoleRepository roleRepository;

    public NewsServiceImpl(NewsRepository newsRepository,
                           UserRepository userRepository,
                           ReadStatusRepository readStatusRepository,
                           PhotoRepository photoRepository,
                           RoleRepository roleRepository) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
        this.readStatusRepository = readStatusRepository;
        this.photoRepository = photoRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public String ping() {
        return "Success!";
    }

    @Override
    public Page<News> getAllNews(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }

    @Override
    public News getNewsById(UUID newsId) {
        return newsRepository.findById(newsId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found. News id: " + newsId));
    }

    @Override
    public News addNews(News news) {
        return newsRepository.save(news);
    }

    @Override
    public void deleteNews(UUID newsId) {
        Optional<News> newsOptional = newsRepository.findById(newsId);
        Optionals.ifPresentOrElse(newsOptional, newsRepository::delete, () -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found. News id: " + newsId);
        });
    }

    @Override
    public List<News> searchNews(String query) {
        return null;
    }

    @Override
    public List<NewsDto> readAllTitles() {
        return newsRepository.readAllTitles();
    }

    @Override
    public List<News> getRecentNewsForSingleAccountAndRole(String role, Integer accountId) {
        List<News> news = newsRepository.findAllByIgnoreCaseAllowedRoleAndReadStatusAccountIdAndReadStatusReadDate(role, accountId, null);
        return news.stream().filter(this::isNew).collect(Collectors.toList());
    }

    @Override
    public List<News> getRecentNewsForSingleAccount(Integer accountId) {
        List<News> news = newsRepository.findAllByReadStatusAccountIdAndReadStatusReadDate(accountId, null);
        return news.stream().filter(this::isNew).collect(Collectors.toList());
    }

    @Override
    public void updateNewsReadStatus(UUID newsId, Integer accountId, ReadStatusDto readStatusDto) {
        Optional<News> newsOptional = newsRepository.findById(newsId);
        Optional<User> userOptional = userRepository.findById(accountId);

        Optionals.ifPresentOrElse(userOptional, User::getId,
                () -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found. User id: " + accountId);
                });

        Optionals.ifPresentOrElse(newsOptional, news -> {
            ReadStatus readStatus = news.getReadStatus();
            readStatus.setReadDate(readStatusDto.getReadDate());
            news.setReadStatus(readStatus);
            newsRepository.save(news);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found. News id: " + newsId);
        });
    }

    @Override
    public Optional<User> findUserById(Integer accountId) {
        return userRepository.findById(accountId);
    }

    @Override
    public Photo getPictureForNewsIdAndPictureIdAndRole(UUID newsId, UUID pictureId, String allowedRole) {
        News news = newsRepository.findById(newsId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found. News id: " + newsId));
        roleRepository.findByRoleName(ROLE_PREFIX + allowedRole.toUpperCase()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found. Role name: " + allowedRole));
        if (!StringUtils.equalsIgnoreCase(allowedRole, news.getAllowedRole()))
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Getting picture is not possible. Role not allowed. Role name: " + allowedRole);
        Optional<Photo> pictureOptional = photoRepository.findByIdAndNewsIdAndIgnoreCaseNewsAllowedRole(pictureId, newsId, allowedRole);
        return pictureOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Picture not found. Picture id: " + pictureId));
    }

    @Override
    public News updateNews(UUID newsId, NewsDetailsDto newsDetailsDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("There are constraint validation errors");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The following news fields are not valid: " + bindingResult.getFieldErrors().stream()
                    .map(FieldError::getField)
                    .collect(Collectors.joining(", ")));
        }
        Optional<News> newsOptional = newsRepository.findById(newsId);

        Optionals.ifPresentOrElse(newsOptional, news -> {
            setNewsFields(newsDetailsDto, news);
            newsRepository.save(news);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found. News id: " + newsId);
        });
        log.info("Updating News for Id : {}", newsId);
        return newsOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found. News id: " + newsId));
    }

    private void setNewsFields(NewsDetailsDto newsDetailsDto, News news) {
        if (org.springframework.util.StringUtils.hasText(newsDetailsDto.getText()))
            news.setText(newsDetailsDto.getText());
        if (org.springframework.util.StringUtils.hasText(newsDetailsDto.getTitle()))
            news.setTitle(newsDetailsDto.getTitle());
        if (org.springframework.util.StringUtils.hasText(newsDetailsDto.getAllowedRole()))
            news.setAllowedRole(newsDetailsDto.getAllowedRole());
        if (org.springframework.util.StringUtils.hasText(newsDetailsDto.getUnAllowedRole()))
            news.setUnAllowedRole(newsDetailsDto.getUnAllowedRole());
        if (Objects.nonNull(newsDetailsDto.getValidFrom()))
            news.setValidFrom(newsDetailsDto.getValidFrom());
        if (Objects.nonNull(newsDetailsDto.getValidFrom()))
            news.setValidTo(newsDetailsDto.getValidTo());
    }

    private boolean isNew(News news) {
        return news.getCreatedDate().toInstant().plus(1, ChronoUnit.DAYS).isAfter(Instant.now());
    }
}
