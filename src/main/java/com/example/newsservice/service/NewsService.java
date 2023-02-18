package com.example.newsservice.service;

import com.example.newsservice.dto.NewsDetailsDto;
import com.example.newsservice.dto.NewsDto;
import com.example.newsservice.dto.ReadStatusDto;
import com.example.newsservice.entity.News;
import com.example.newsservice.entity.Photo;
import com.example.newsservice.security.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsService {

    public String ping();

    public Page<NewsDetailsDto> getAllNews(Pageable pageable);

    public NewsDetailsDto getNewsById(UUID newsId);

    public NewsDetailsDto addNews(NewsDetailsDto news, BindingResult bindingResult);

    public void deleteNews(UUID newsId);

    public List<News> searchNews(String query);

    public List<NewsDto> readAllTitles();

    public List<News> getRecentNewsForSingleAccountAndRole(String role, Integer accountId);

    public List<News> getRecentNewsForSingleAccount(Integer accountId);

    public void updateNewsReadStatus(UUID newsId, Integer accountId, ReadStatusDto readStatusDto);

    public Optional<User> findUserById(Integer accountId);

    public Photo getPictureForNewsIdAndPictureIdAndRole(UUID newsId, UUID pictureId, String allowedRole);

    NewsDetailsDto updateNews(UUID newsId, NewsDetailsDto newsDetails, BindingResult bindingResult);

    Optional<NewsDetailsDto> patchNewsById(UUID newsId, NewsDetailsDto newsDetailsDto);
}
