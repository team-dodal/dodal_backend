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
@Schema(description = "도전방 생성 요청")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChallengeRoomCreateRequest {

    @NotBlank(message = "tag_value는 필수값입니다.")
    @Schema(name =  "tag_value", example = "001001")
    private String tagValue;

    @NotBlank(message = "title은 필수값입니다.")
    @Schema(description = "도전방 제목", example = "테스트 제목 입니다.")
    private String title;

    @Schema(description = "썸네일 이미지 URL", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com")
    private String thumbnailImgUrl;

    @Size(min = 1, max = 500, message = "content는 1자 ~ 500자 사이여야 합니다.")
    @Schema(description =  "도전방 설명", example = "이런 분들에게 추천해요! \n - 자격증 시험이 얼마 남지 않은 분 \n - 꾸준하게 공부하고 싶은 분 ")
    private String content;

    @Range(min = 5, max = 50, message = "recruit_cnt는 5 ~ 50 사이여야 합니다.")
    @Schema(description = "모집 인원", example = "15")
    private int recruitCnt;

    @Range(min = 1, max = 7, message = "cert_cnt는 1 ~ 7 사이여야 합니다.")
    @Schema(description = "인증 횟수", example = "2")
    private int certCnt;

    @Size(min = 1, max = 500, message = "cert_content은 1자 ~ 500자 사이여야 합니다.")
    @Schema(description = "인증 관련 설명 글", example = "이렇게 인증하세요.")
    private String certContent;


    @Schema(description = "인증 성공 예시 이미지 URL", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com")
    private String certCorrectImgUrl;

    @Schema(description = "인증 실패 예시 이미지 URL", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com")
    private String certWrongImgUrl;

}
