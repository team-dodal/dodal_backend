package com.dodal.meet.controller.request.challengeroom;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Schema(description = "도전방 수정 요청")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChallengeRoomUpdateRequest {


    @NotBlank(message = "tag_value는 필수값입니다.")
    @Schema(name =  "tag_value", example = "001001")
    private String tagValue;

    @NotBlank(message = "title은 필수값입니다.")
    @Schema(name =  "title", example = "매일매일 자격증 공부!")
    private String title;

    @NotBlank(message = "thumbnail_img_url은 필수값입니다.")
    @Schema(name = "thumbnail_img_url", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com/test")
    private String thumbnailImgUrl;

    @Size(min = 1, max = 500, message = "content는 1자 ~ 500자 사이여야 합니다.")
    @Schema(name =  "content", example = "이런 분들에게 추천해요! \n - 자격증 시험이 얼마 남지 않은 분 \n - 꾸준하게 공부하고 싶은 분 ")
    private String content;

    @Range(min = 1, max = 20, message = "recruit_cnt는 1 ~ 20 사이여야 합니다.")
    @Schema(name =  "recruit_cnt", example = "10")
    private int recruitCnt;

    @Range(min = 1, max = 7, message = "cert_cnt는 1 ~ 7 사이여야 합니다.")
    @Schema(name =  "cert_cnt", example = "3")
    private int certCnt;

    @Size(min = 1, max = 500, message = "cert_content은 1자 ~ 500자 사이여야 합니다.")
    @Schema(name =  "cert_content", example = "인증은 아래의 예시 이미지와 같이 업로드해야 합니다.")
    private String certContent;

    @Schema(name = "cert_correct_img_url", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com/test")
    private String certCorrectImgUrl;

    @Schema(name = "cert_wrong_img_url", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com/test")
    private String certWrongImgUrl;
}
