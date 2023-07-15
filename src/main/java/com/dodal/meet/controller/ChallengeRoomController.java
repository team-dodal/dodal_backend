package com.dodal.meet.controller;

import com.dodal.meet.controller.request.challengeRoom.ChallengeRoomCreateRequest;
import com.dodal.meet.controller.response.Response;
import com.dodal.meet.controller.response.challenge.ChallengeCreateResponse;
import com.dodal.meet.service.ChallengeRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "ChallengeRoom", description = "도전방 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
@Validated
public class ChallengeRoomController {

    private final ChallengeRoomService challengeRoomService;

    @Operation(summary = "도전방 생성 API"
            , description = "도전방을 생성한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - INVALID_REQUEST_FIELD", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @PostMapping(value = "/challenge/room", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<Response<ChallengeCreateResponse>>> createChallengeRoom(

            @NotBlank(message = "tag_value는 필수값입니다.")
            @Schema(name =  "tag_value", example = "001001")
            @RequestParam(name = "tag_value") String tagValue,

            @NotBlank(message = "title은 필수값입니다.")
            @Schema(name =  "title", example = "매일매일 자격증 공부!")
            @RequestParam(name = "title") String title,

            @Schema(name = "thumbnail_img")
            @Parameter(name = "thumbnail_img", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(name = "thumbnail_img") MultipartFile thumbnailImg,

            @Size(min = 1, max = 500, message = "content는 1자 ~ 500자 사이여야 합니다.")
            @Schema(name =  "content", example = "이런 분들에게 추천해요! \n - 자격증 시험이 얼마 남지 않은 분 \n - 꾸준하게 공부하고 싶은 분 ")
            @RequestParam(name = "content") String content,

            @Range(min = 5, max = 50, message = "recruit_cnt는 5 ~ 50 사이여야 합니다.")
            @Schema(name =  "recruit_cnt", example = "10")
            @RequestParam(name = "recruit_cnt") int recruitCnt,

            @Range(min = 1, max = 7, message = "cert_cnt는 1 ~ 7 사이여야 합니다.")
            @Schema(name =  "cert_cnt", example = "3")
            @RequestParam(name = "cert_cnt") int certCnt,

            @Size(min = 1, max = 500, message = "cert_content은 1자 ~ 500자 사이여야 합니다.")
            @Schema(name =  "cert_content", example = "인증은 아래의 예시 이미지와 같이 업로드해야 합니다.")
            @RequestParam(name = "cert_content") String certContent,

            @Schema(name = "cert_correct_img")
            @Parameter(name = "cert_correct_img", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(name = "cert_correct_img") MultipartFile certCorrectImg,

            @Schema(name = "cert_wrong_img")
            @Parameter(name = "cert_wrong_img", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(name = "cert_wrong_img") MultipartFile certWrongImg,

            @Size(min = 1, max = 500, message = "warn_content는 1자 ~ 500자 사이여야 합니다.")
            @Schema(name =  "warn_content", example = "10번 이상 인증 실패 시 강퇴 처리 됩니다. \n 꾸준히 인증 해주세요.")
            @RequestParam(name = "warn_content") String warnContent,

            final Authentication authentication
    ) {
        final ChallengeRoomCreateRequest challengeRoomCreateRequest = ChallengeRoomCreateRequest.builder()
                .tagValue(tagValue)
                .title(title)
                .thumbnailImg(thumbnailImg)
                .content(content)
                .recruitCnt(recruitCnt)
                .certCnt(certCnt)
                .certContent(certContent)
                .certCorrectImg(certCorrectImg)
                .certWrongImg(certWrongImg)
                .warnContent(warnContent)
                .build();
        Link selfRel = linkTo(methodOn(ChallengeRoomController.class).createChallengeRoom(tagValue, title, thumbnailImg, content,
                recruitCnt, certCnt, certContent, certCorrectImg, certWrongImg, warnContent, authentication)).withSelfRel();
        return new ResponseEntity<>(EntityModel.of(Response.success(challengeRoomService.createChallengeRoom(challengeRoomCreateRequest, authentication)), selfRel), HttpStatus.CREATED);
    }
}
