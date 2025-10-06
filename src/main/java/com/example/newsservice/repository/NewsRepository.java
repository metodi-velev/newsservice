package com.example.newsservice.repository;

import com.example.newsservice.dto.NewsDto;
import com.example.newsservice.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NewsRepository extends JpaRepository<News, UUID> {
    @Query(value = "select new com.example.newsservice.dto.NewsDto(n.title) from News n")
    List<NewsDto> readAllTitles();

    List<News> findAllByIgnoreCaseAllowedRoleAndReadStatusAccountIdAndReadStatusReadDate(String allowedRole, Integer accountId, OffsetDateTime readDate);

    List<News> findAllByReadStatusAccountIdAndReadStatusReadDate(Integer accountId, OffsetDateTime readDate);

    Optional<News> findByLinkToPhoto(String linkToPhoto);
}