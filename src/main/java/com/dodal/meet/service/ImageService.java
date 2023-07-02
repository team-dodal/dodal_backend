package com.dodal.meet.service;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.dodal.meet.controller.request.user.UserProfileRequest;
import com.dodal.meet.controller.response.user.UserProfileResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.User;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.repository.UserEntityRepository;
import com.dodal.meet.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final AmazonS3Client amazonS3Client;
    private final UserEntityRepository userEntityRepository;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    @Transactional
    public UserProfileResponse uploadImage(UserProfileRequest userProfileRequest) {
        if (userProfileRequest == null) {
            throw new DodalApplicationException(ErrorCode.INVALID_IMAGE_REQUEST);
        }

        final MultipartFile multipartFile = userProfileRequest.getProfile();
        final String originalFileName = multipartFile.getOriginalFilename();
        final String uniqueFileName = getUniqueFileName(originalFileName);

        UserProfileResponse response = new UserProfileResponse();
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(multipartFile.getContentType());
            objectMetadata.setContentLength(multipartFile.getInputStream().available());

            amazonS3Client.putObject(bucket, uniqueFileName, multipartFile.getInputStream(), objectMetadata);

            final String s3ImageUrl = amazonS3Client.getUrl(bucket, uniqueFileName).toString();
            response.setProfileUrl(s3ImageUrl);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new DodalApplicationException(ErrorCode.INVALID_IMAGE_REQUEST);
        }
        return response;
    }

    private String getUniqueFileName(String originalFileName) {
        return UUID.randomUUID() +"." + extractExtension(originalFileName);
    }

    private String extractExtension(String originalFileName) {
        int index = originalFileName.lastIndexOf('.');

        return originalFileName.substring(index, originalFileName.length());
    }
}
