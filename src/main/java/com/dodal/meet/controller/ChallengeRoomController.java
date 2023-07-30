package com.dodal.meet.controller;

import com.dodal.meet.controller.request.challengeRoom.ChallengeNotiRequest;
import com.dodal.meet.controller.request.challengeRoom.ChallengeRoomCreateRequest;
import com.dodal.meet.controller.request.challengeRoom.ChallengeRoomSearchCategoryRequest;
import com.dodal.meet.controller.response.Response;
import com.dodal.meet.controller.response.challenge.ChallengeCreateResponse;
import com.dodal.meet.controller.response.challenge.ChallengeRoomDetailResponse;
import com.dodal.meet.controller.response.challenge.ChallengeRoomSearchResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
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


    @Operation(summary = "도전방 조회 API"
            , description = "홈 화면 - 요청에 따라 최근 도전: recency, 인기있는 도전 : popularity, 관심있는 도전 : interest 정보를 반환한다. (관심있는 도전의 경우 조회 할 태그 값을 파라미터에 전달 필수) pageable의 경우 page : 0부터 시작, 조회할 개수 size를 입력한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "NOT_FOUND_TAG", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @GetMapping("/challenge/rooms")
    public ResponseEntity<EntityModel<Response<Page<ChallengeRoomSearchResponse>>>> getChallengeRoom(@Schema(description = "조회 조건", example = "recency") @RequestParam(name = "condition") String condition,
                                                                                                     @Schema(description = "태그 코드 값", example = "001001") @RequestParam(name = "tag_value", required = false) String tagValue,
                                                                                                     @Schema(description = "요청 페이지 번호", example = "0") @RequestParam(name = "page") Integer page ,
                                                                                                     @Schema(description = "요청 페이지 사이즈", example = "3") @RequestParam(name = "page_size") Integer pageSize,
                                                                                                     Authentication authentication) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return new ResponseEntity<>(EntityModel.of(Response.success(challengeRoomService.getChallengeRooms(condition, tagValue, pageable, authentication))), HttpStatus.OK);
    }

    @Operation(summary = "도전방 카테고리별 조회 API"
            , description = "홈 화면 - 전체 / 카테고리 / 태그 별 도전방 조회 정보를 반환한다.\n" +
            "카테고리 / 태그 값 정보가 없으면 전체 조회\n" +
            "condition_code : 0(인기순) , 1(최신순), 2(인원 많은 순), 3(인원 적은순)\n" +
            "cert_cnt_list : 빈도 선택 값 아무 선택이 없으면 전체 조회 그 외는 빈도 수 리스트 요청",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "INVALID_REQUEST_FIELD", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @GetMapping("/challenge/rooms/category")
    public ResponseEntity<EntityModel<Response<Page<ChallengeRoomSearchResponse>>>> getChallengeRoomByCategory(@Schema(description = "카테고리 코드 값", example = "001") @RequestParam(name="category_value",required = false) String categoryValue,
                                                                                                               @Schema(description = "태그 코드 값", example = "001001") @RequestParam(name = "tag_value", required = false) String tagValue,
                                                                                                               @Schema(description = "조회 코드 값 - 0(인기순) , 1(최신순), 2(인원 많은 순), 3(인원 적은순) ", allowableValues = {"0", "1", "2", "3"}, example = "0") @RequestParam(name = "condition_code", defaultValue = "0") String conditionCode,
                                                                                                               @Schema(description = "도전 빈도 리스트", example = "[1, 3, 5]", type = "array") @RequestParam(name = "cert_cnt_list") List<Integer> certCntList,
                                                                                                               @Schema(description = "요청 페이지 번호", example = "0") @RequestParam(name = "page") Integer page,
                                                                                                               @Schema(description = "요청 페이지 사이즈", example = "3") @RequestParam(name = "page_size") Integer pageSize,
                                                                                                               Authentication authentication) {
        ChallengeRoomSearchCategoryRequest request = ChallengeRoomSearchCategoryRequest
                .builder()
                .tagValue(tagValue)
                .categoryValue(categoryValue)
                .conditionCode(conditionCode)
                .certCntList(certCntList)
                .page(page)
                .pageSize(pageSize)
                .build();
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());
        return new ResponseEntity<>(EntityModel.of(Response.success(challengeRoomService.getChallengeRoomsByCategory(request, pageable, authentication))), HttpStatus.OK);
    }

    @Operation(summary = "도전방 상세 조회 API"
            , description = "도전방 상세 정보를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_ROOM", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @GetMapping("/challenge/rooms/{room_id}")
    public ResponseEntity<EntityModel<Response<ChallengeRoomDetailResponse>>> getChallengeRoomDetail(@PathVariable(name = "room_id") Integer roomId, Authentication authentication) {
        return new ResponseEntity<>(EntityModel.of(Response.success(challengeRoomService.getChallengeRoomDetail(roomId, authentication))), HttpStatus.OK);
    }

    @Operation(summary = "도전방 가입 API"
            , description = "도전방에 가입한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_ROOM", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @PostMapping("/challenge/rooms/{room_id}/join")
    public ResponseEntity<EntityModel<Response<Void>>> joinChallengeRoom(@PathVariable(name = "room_id") Integer roomId, Authentication authentication) {
        challengeRoomService.joinChallengeRoom(roomId, authentication);
        return new ResponseEntity<>(EntityModel.of(Response.success()), HttpStatus.OK);
    }

    @Operation(summary = "도전방 탈퇴 API"
            , description = "도전방에 탈퇴한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_ROOM", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @DeleteMapping("/challenge/rooms/{room_id}/join")
    public ResponseEntity<EntityModel<Response<Void>>> leaveChallengeRoom(@PathVariable(name = "room_id") Integer roomId, Authentication authentication) {
        challengeRoomService.leaveChallengeRoom(roomId, authentication);
        return new ResponseEntity<>(EntityModel.of(Response.success()), HttpStatus.NO_CONTENT);
    }


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
            @RequestPart(name = "thumbnail_img", required = false) MultipartFile thumbnailImg,

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
                .build();
        Link selfRel = linkTo(methodOn(ChallengeRoomController.class).createChallengeRoom(tagValue, title, thumbnailImg, content,
                recruitCnt, certCnt, certContent, certCorrectImg, certWrongImg, authentication)).withSelfRel();
        return new ResponseEntity<>(EntityModel.of(Response.success(challengeRoomService.createChallengeRoom(challengeRoomCreateRequest, authentication)), selfRel), HttpStatus.CREATED);
    }

    @Operation(summary = "북마크 등록 API"
            , description = "도전방에 북마크를 등록한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "BOOKMARK_ALREADY_EXIST", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @PostMapping("/challenge/room/{room_id}/bookmark")
    public ResponseEntity<EntityModel<Response<Void>>> createBookmark(@PathVariable(name = "room_id") Integer roomId, Authentication authentication) {
        Link selfRel = linkTo(methodOn(ChallengeRoomController.class).createBookmark(roomId, authentication)).withSelfRel();
        challengeRoomService.createBookmark(roomId, authentication);
        return new ResponseEntity<>(EntityModel.of(Response.success(), selfRel), HttpStatus.CREATED);
    }


    @Operation(summary = "북마크 삭제 API"
            , description = "도전방에 북마크를 삭제한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_BOOKMARK", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @DeleteMapping("/challenge/room/{room_id}/bookmark")
    public ResponseEntity<EntityModel<Response<Void>>> deleteBookmark(@PathVariable(name = "room_id") Integer roomId, Authentication authentication) {
        Link selfRel = linkTo(methodOn(ChallengeRoomController.class).deleteBookmark(roomId, authentication)).withSelfRel();
        challengeRoomService.deleteBookmark(roomId, authentication);
        return new ResponseEntity<>(EntityModel.of(Response.success(), selfRel), HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "도전방 인증 API"
            , description = "도전방에 이미지 / 인증글을 통해 인증한다. (인증글은 100자 이내)",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "BOOKMARK_ALREADY_EXIST", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @PostMapping(value = "/challenge/room/{room_id}/certification", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<Response<Void>>> createCertification(@PathVariable(name = "room_id") Integer roomId,
                                                                           @Schema(name = "certification_img")
                                                                           @Parameter(name = "certification_img", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                                           @RequestPart(name = "certification_img") MultipartFile certificationImg,

                                                                           @Size(min = 1, max = 100, message = "content는 1자 ~ 100자 사이여야 합니다.")
                                                                           @Schema(name =  "content", example = "인증합니다.")
                                                                           @RequestParam(name = "content") String content,
                                                                           Authentication authentication) {
        Link selfRel = linkTo(methodOn(ChallengeRoomController.class).createCertification(roomId, certificationImg, content, authentication)).withSelfRel();
        challengeRoomService.createCertification(roomId, certificationImg, content, authentication);
        return new ResponseEntity<>(EntityModel.of(Response.success(), selfRel), HttpStatus.CREATED);
    }

    @Operation(summary = "공지사항 등록 API"
            , description = "도전방에 공지사항을 등록한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "BOOKMARK_ALREADY_EXIST", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @PostMapping("/challenge/room/{room_id}/noti")
    public ResponseEntity<EntityModel<Response<Void>>> registNoti(@PathVariable(name = "room_id") Integer roomId, @RequestBody ChallengeNotiRequest challengeNotiRequest, Authentication authentication) {
        Link selfRel = linkTo(methodOn(ChallengeRoomController.class).registNoti(roomId, challengeNotiRequest, authentication)).withSelfRel();
        challengeRoomService.registNoti(roomId, challengeNotiRequest, authentication);
        return new ResponseEntity<>(EntityModel.of(Response.success(), selfRel), HttpStatus.CREATED);
    }
}
