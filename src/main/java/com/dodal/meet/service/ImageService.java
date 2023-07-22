package com.dodal.meet.service;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.dodal.meet.controller.request.user.UserProfileRequest;
import com.dodal.meet.controller.response.user.UserProfileResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public UserProfileResponse uploadProfileImg(UserProfileRequest userProfileRequest) {
        if (userProfileRequest == null) {
            throw new DodalApplicationException(ErrorCode.INVALID_IMAGE_REQUEST);
        }

        final MultipartFile multipartFile = userProfileRequest.getProfile();
        UserProfileResponse response = new UserProfileResponse();

        final String s3ImageUrl = getS3ImgUrl(multipartFile);
        response.setProfileUrl(s3ImageUrl);

        return response;
    }

    public void deleteImg(final String imgUrl) {
        try {
            amazonS3Client.deleteObject(bucket, parsingFileName(imgUrl));
        } catch (AmazonServiceException e) {
            log.error("deleteImg Error : {} {}", "AmazonServiceException",  e.getMessage());
        } catch (SdkClientException e) {
            log.error("deleteImg Error : {} {}", "SdkClientException",  e.getMessage());
        }

    }

    private String parsingFileName(final String imgUrl) {
        try {
            return imgUrl.split("/")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new DodalApplicationException(ErrorCode.INVALID_IMAGE_REQUEST);
        }
    }

    public String uploadMultipartFile(MultipartFile multipartFile) {
        if (multipartFile == null) {
            throw new DodalApplicationException(ErrorCode.INVALID_IMAGE_REQUEST);
        }
        return getS3ImgUrl(multipartFile);
    }

    private String getS3ImgUrl(MultipartFile multipartFile) {

        final String originalFileName = multipartFile.getOriginalFilename();
        final String uniqueFileName = getUniqueFileName(originalFileName);
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(multipartFile.getContentType());
            objectMetadata.setContentLength(multipartFile.getInputStream().available());
            amazonS3Client.putObject(bucket, uniqueFileName, multipartFile.getInputStream(), objectMetadata);
            final String s3ImageUrl = amazonS3Client.getUrl(bucket, uniqueFileName).toString();

            return s3ImageUrl;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new DodalApplicationException(ErrorCode.INVALID_IMAGE_REQUEST);
        }
    }

    private String getUniqueFileName(String originalFileName) {
        return UUID.randomUUID() +"." + extractExtension(originalFileName);
    }

    private String extractExtension(String originalFileName) {
        int index = originalFileName.lastIndexOf('.');

        return originalFileName.substring(index, originalFileName.length());
    }
}
