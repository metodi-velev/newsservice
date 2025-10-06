package com.example.newsservice.repository;

import com.example.newsservice.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    Optional<Photo> findByPhotoName(String name);
    Optional<Photo> findByIdAndNewsIdAndIgnoreCaseNewsAllowedRole(UUID id, UUID newsId, String allowedRole);
    Optional<Photo> findByLinkToPhoto(String linkToPhoto);
}