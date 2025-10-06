package com.example.newsservice.service;

import com.example.newsservice.entity.Photo;
import com.example.newsservice.exception.NotAnImageFileException;
import com.example.newsservice.exception.PhotoCreationException;
import com.example.newsservice.repository.PhotoRepository;
import com.example.newsservice.utils.PhotoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.ImageReadException;
import org.springframework.beans.factory.annotation.Autowired;
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

@Slf4j
@Service
public class PhotoServiceImpl implements PhotoService {
    public static final String NOT_AN_IMAGE_FILE = " is not an image file. Please upload an image file";

    private final PhotoRepository photoRepository;
    private final PhotoUtils photoUtils;

    @Autowired
    public PhotoServiceImpl(PhotoRepository photoRepository, PhotoUtils photoUtils) {
        this.photoRepository = photoRepository;
        this.photoUtils = photoUtils;
    }

    @Override
    public void uploadFile(String uploadDir, String fileName,
                           MultipartFile multipartFile) throws IOException, NotAnImageFileException {
        getPhoto(uploadDir, fileName, multipartFile);
    }

    @Override
    public void getPhoto(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException, NotAnImageFileException {
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

    @Override
    public Photo getOrCreatePhoto(String photoName, String linkToPhoto) {
        return photoRepository.findByLinkToPhoto(linkToPhoto)
                .orElseGet(() -> {
                    try {
                        Photo newPhoto = photoRepository.save(Photo.builder()
                                .photoName(photoName)
                                .metaData(photoUtils.getMetadataFromPhoto(linkToPhoto))
                                .photoData(new byte[]{})
                                .linkToPhoto(linkToPhoto)
                                .build()
                        );
                        return photoRepository.save(newPhoto);
                    } catch (IOException | ImageReadException e) {
                        log.error("Failed to create photo with link: {}", linkToPhoto, e);
                        throw new PhotoCreationException("Failed to create photo: " + linkToPhoto, e);
                    }
                });
    }
}
