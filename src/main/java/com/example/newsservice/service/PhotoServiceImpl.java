package com.example.newsservice.service;

import com.example.newsservice.exception.NotAnImageFileException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import static org.springframework.http.MediaType.*;

@Service
public class PhotoServiceImpl implements PhotoService {
    public static final String NOT_AN_IMAGE_FILE = " is not an image file. Please upload an image file";

    @Override
    public void uploadFile(String uploadDir, String fileName,
                           MultipartFile multipartFile) throws IOException, NotAnImageFileException {
        getPhoto(uploadDir, fileName, multipartFile);
    }

    public static void getPhoto(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException, NotAnImageFileException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(multipartFile.getContentType())) {
            throw new NotAnImageFileException(multipartFile.getOriginalFilename() + NOT_AN_IMAGE_FILE);
        }

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
    }
}
