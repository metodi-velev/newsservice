package com.example.newsservice.utils;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Component
public class PhotoUtils {

    public ImageMetadata getMetadataFromPhoto(String linkToPhoto) throws IOException, ImageReadException {
        URL url = new URL(linkToPhoto);
        String tDir = System.getProperty("java.io.tmpdir");
        String path = tDir + "tmp" + ".jpg";
        File file = new File(path);
        file.deleteOnExit();
        FileUtils.copyURLToFile(url, file);
        return Imaging.getMetadata(file);
    }
}
