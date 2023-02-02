package com.example.newsservice.repository;

import com.example.newsservice.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    Optional<Photo> findByPhotoName(String name);
}