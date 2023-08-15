package com.dodal.meet.controller.request.challengeroom;


import com.dodal.meet.valid.NullOrNotBlank;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Schema(description = "도전방 수정 요청")
@ToString
@AllArgsConstructor
@Builder
public class ChallengeRoomUpdateRequest {


    @NullOrNotBlank
    @Schema(name =  "tag_value", example = "001001")
    private String tagValue;

    @NullOrNotBlank
    @Schema(name =  "title", example = "매일매일 자격증 공부!")
    private String title;

    @Schema(name = "thumbnail_img")
    @Parameter(name = "thumbnail_img", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
    private MultipartFile thumbnailImg;


    //            @Size(min = 1, max = 500, message = "content는 1자 ~ 500자 사이여야 합니다.")
    @NullOrNotBlank
    @Schema(name =  "content", example = "이런 분들에게 추천해요! \n - 자격증 시험이 얼마 남지 않은 분 \n - 꾸준하게 공부하고 싶은 분 ")
    private String content;

    //            @Range(min = 5, max = 50, message = "recruit_cnt는 5 ~ 50 사이여야 합니다.")

    @NullOrNotBlank
    @Schema(name =  "recruit_cnt", example = "10")
    private int recruitCnt;

//            @Range(min = 1, max = 7, message = "cert_cnt는 1 ~ 7 사이여야 합니다.")
    @NullOrNotBlank
    @Schema(name =  "cert_cnt", example = "3")
    private int certCnt;

    //            @Size(min = 1, max = 500, message = "cert_content은 1자 ~ 500자 사이여야 합니다.")
    @NullOrNotBlank
    @Schema(name =  "cert_content", example = "인증은 아래의 예시 이미지와 같이 업로드해야 합니다.")
    private String certContent;

    @Schema(name = "cert_correct_img")
    @Parameter(name = "cert_correct_img", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
    private MultipartFile certCorrectImg;

    @Schema(name = "cert_wrong_img")
    @Parameter(name = "cert_wrong_img", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
    private MultipartFile certWrongImg;
}
