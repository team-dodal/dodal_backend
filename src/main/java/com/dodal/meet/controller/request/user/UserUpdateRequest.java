package com.dodal.meet.controller.request.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@Schema(description = "유저 정보 업데이트 요청")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class UserUpdateRequest {
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s]{1,16}$", message = "nickname은 한글, 영어, 숫자로만 이루어진 1자리 이상 16자리 이하의 값이어야 합니다.")
    @Schema(name =  "nickname", example = "노래하는 어피치")
    private String nickname;

    @Pattern(regexp = "^(.{0}|.{1,40})$", message = "값은 40자리 이하이어야 합니다.")
    @Schema(name =  "content", example = "안녕하세요")
    private String content;

    @ArraySchema(schema = @Schema(name =  "tag_list", type = "array", example = "001001"))
    private List<String> tagList;

    @Schema(name = "profile_url", example = "https://s3.console.aws.amazon.com/s3/object/dodal-bucket?region=ap-northeast-2&prefix=15e4f096-2a33-4634-8ba1-88b351ee0a95..ico")
    private String profileUrl;
}
