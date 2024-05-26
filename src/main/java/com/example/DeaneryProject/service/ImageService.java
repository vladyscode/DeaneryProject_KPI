package com.example.DeaneryProject.service;

import com.example.DeaneryProject.model.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {
    public Image getDefaultImage() throws IOException {
        ClassPathResource imgFile = new ClassPathResource("static/images/default_img.jpg");
        byte[] bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());

        Image image = new Image();
        image.setBytes(bytes);
        image.setContentType("image/jpeg");
        image.setSize((long) bytes.length);
        image.setOriginalFileName(imgFile.getFilename());

        return image;
    }
}