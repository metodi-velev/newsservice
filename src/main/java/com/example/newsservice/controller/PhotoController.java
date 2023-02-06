package com.example.newsservice.controller;

import com.example.newsservice.exception.NotAnImageFileException;
import com.example.newsservice.service.PhotoService;
import io.swagger.annotations.Api;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/picture")
@Api(description = "APIs for photo service")
public class PhotoController {

    private static final Logger LOG = LoggerFactory.getLogger(PhotoController.class);

    private final PhotoService photoService;


    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    private final String UPLOAD_DIR = "src/main/resources/uploads/";

    @PreAuthorize("hasRole('PUBLISHER')")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadPhoto(@RequestParam("image") @NonNull MultipartFile multipartFile) throws IOException, NotAnImageFileException {

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        photoService.uploadFile(UPLOAD_DIR, fileName, multipartFile);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
