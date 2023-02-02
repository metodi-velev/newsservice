package com.example.newsservice.repository;

import com.example.newsservice.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

}