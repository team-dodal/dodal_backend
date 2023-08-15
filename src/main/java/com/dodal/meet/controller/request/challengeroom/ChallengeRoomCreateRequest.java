package com.dodal.meet.controller.request.challengeroom;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Schema(description = "도전방 생성 요청")
@ToString
@AllArgsConstructor
@Builder
public class ChallengeRoomCreateRequest {

    private String tagValue;

    private String title;

    private String content;

    private int certCnt;

    private MultipartFile thumbnailImg;

    private int recruitCnt;

    private String certContent;

    private MultipartFile certCorrectImg;

    private MultipartFile certWrongImg;

}