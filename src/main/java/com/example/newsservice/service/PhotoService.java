package com.example.newsservice.service;

import com.example.newsservice.entity.Photo;
import com.example.newsservice.exception.NotAnImageFileException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoService {
    void uploadFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException, NotAnImageFileException;

    void getPhoto(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException, NotAnImageFileException;

    Photo getOrCreatePhoto(String photoName, String linkToPhoto);
}
