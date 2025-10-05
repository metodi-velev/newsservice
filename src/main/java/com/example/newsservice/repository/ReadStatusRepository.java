package com.example.newsservice.repository;

import com.example.newsservice.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

}