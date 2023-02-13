package com.example.newsservice.service;

import com.example.newsservice.dto.NewsDetailsDto;
import com.example.newsservice.dto.NewsDto;
import com.example.newsservice.dto.ReadStatusDto;
import com.example.newsservice.entity.News;
import com.example.newsservice.entity.Photo;
import com.example.newsservice.security.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsService {

    public String ping();

    public Page<News> getAllNews(Pageable pageable);

    public News getNewsById(UUID newsId);

    public News addNews(News news);

    public void deleteNews(UUID newsId);

    public List<News> searchNews(String query);

    public List<NewsDto> readAllTitles();

    public List<News> getRecentNewsForSingleAccountAndRole(String role, Integer accountId);

    public List<News> getRecentNewsForSingleAccount(Integer accountId);

    public void updateNewsReadStatus(UUID newsId, Integer accountId, ReadStatusDto readStatusDto);

    public Optional<User> findUserById(Integer accountId);

    public Photo getPictureForNewsIdAndPictureIdAndRole(UUID newsId, UUID pictureId, String allowedRole);

    News updateNews(UUID newsId, NewsDetailsDto newsDetails);
}
