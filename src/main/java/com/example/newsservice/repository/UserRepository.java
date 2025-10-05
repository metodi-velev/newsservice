package com.example.newsservice.repository;

import com.example.newsservice.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    @Query(value = "select u.id from User u where u.username = :userName")
    Integer getUserAccountId(@Param("userName") String userName);
}
