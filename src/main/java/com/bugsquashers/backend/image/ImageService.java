package com.bugsquashers.backend.image;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;
    private final S3Template s3Template;

    private final String SAVE_DIR = "review-img";

    // 이미지 여러개 업로드
    public List<String> saveImages(MultipartFile[] multipartFiles) {
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String imageUrl = saveImage(multipartFile);
            imageUrls.add(imageUrl);
        }
        return imageUrls;
    }

    // 이미지 1개 업로드
    public String saveImage(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        String fileExt = fileName.substring(fileName.lastIndexOf("."));
        String uuidName = UUID.randomUUID() + fileExt;

        if (!Objects.requireNonNull(multipartFile.getContentType()).contains("image/")) {
            throw new IllegalArgumentException("허용되지 않는 형식의 파일입니다: " + multipartFile.getContentType());
        }

        String saveDirWithUuidName = SAVE_DIR + "/" + uuidName;

        try {
            InputStream is = multipartFile.getInputStream();
            S3Resource s3Resource = s3Template.upload(bucketName, saveDirWithUuidName, is, ObjectMetadata.builder().contentType(multipartFile.getContentType()).build());

            return s3Resource.getURL().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
