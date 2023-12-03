package com.dodal.meet.service;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.dodal.meet.controller.request.user.UserProfileRequest;
import com.dodal.meet.controller.response.user.UserProfileResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {
    private final UserService userService;

    private final AmazonS3Client amazonS3Client;

    public ImageService(@Lazy UserService userService, AmazonS3Client amazonS3Client) {
        this.userService = userService;
        this.amazonS3Client = amazonS3Client;
    }

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    @Deprecated(since = "AWS 이미지 업로드 기능을 preSignedUrl로 변경하면서 Deprecated 됨")
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
        if (imgUrl.indexOf("s3") == -1) {
            return;
        }

        try {
            amazonS3Client.deleteObject(bucket, parsingFileName(imgUrl));
        } catch (AmazonServiceException e) {
            log.error("deleteImg Error : {} {}", "AmazonServiceException",  e.getMessage());
        } catch (SdkClientException e) {
            log.error("deleteImg Error : {} {}", "SdkClientException",  e.getMessage());
        }

    }
    
    @Transactional
    public String getPresignedUrl(final String fileName, final User user) {
        userService.getCachedUserEntity(user);

        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60; // 1시간
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT).withExpiration(expiration);

        generatePresignedUrlRequest.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toExternalForm();
    }


    private String parsingFileName(final String imgUrl) {
        try {
            return imgUrl.split("/")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new DodalApplicationException(ErrorCode.INVALID_IMAGE_REQUEST);
        }
    }

    @Deprecated(since = "AWS 이미지 업로드 기능을 preSignedUrl로 변경하면서 Deprecated 됨")
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
