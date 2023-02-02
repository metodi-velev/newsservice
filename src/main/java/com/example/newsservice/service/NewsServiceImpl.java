package com.example.newsservice.service;

import com.example.newsservice.dto.NewsDto;
import com.example.newsservice.dto.ReadStatusDto;
import com.example.newsservice.entity.News;
import com.example.newsservice.entity.ReadStatus;
import com.example.newsservice.repository.NewsRepository;
import com.example.newsservice.repository.ReadStatusRepository;
import com.example.newsservice.repository.UserRepository;
import com.example.newsservice.security.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Optionals;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;

    public NewsServiceImpl(NewsRepository newsRepository,
                           UserRepository userRepository,
                           ReadStatusRepository readStatusRepository) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
        this.readStatusRepository = readStatusRepository;
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
    public Optional<News> getNewsById(UUID newsId) {
        return newsRepository.findById(newsId);
    }

    @Override
    public News addNews(News news) {
        return newsRepository.save(news);
    }

    @Override
    public void deleteNews(News news) {
        newsRepository.delete(news);
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found. UUID: " + newsId);
        });
    }

    public Optional<User> findUserById(Integer accountId) {
        return userRepository.findById(accountId);
    }

    private boolean isNew(News news) {
        return news.getCreatedDate().toInstant().plus(1, ChronoUnit.DAYS).isAfter(Instant.now());
    }
}
