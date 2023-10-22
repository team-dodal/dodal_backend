package com.dodal.meet.controller;


import com.dodal.meet.controller.request.feed.CommentCreateRequest;
import com.dodal.meet.controller.response.ResponseFail;
import com.dodal.meet.controller.response.ResponseSuccess;
import com.dodal.meet.controller.response.feed.FeedResponse;
import com.dodal.meet.model.User;
import com.dodal.meet.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Feed", description = "Feed API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class FeedController {

    private final FeedService feedService;

    @Operation(summary = "도전방 Feed 조회 API"
            , description = "인증에 성공한 도전 인증 피드 리스트들을 보여준다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - NOT_FOUND_ROOM", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/feeds/{room_id}")
    public ResponseEntity<ResponseSuccess<Page<FeedResponse>>> getRoomFeeds(
                                                                        @PathVariable("room_id") final Integer roomId,
                                                                        @Schema(description = "요청 페이지 번호", example = "0") @RequestParam(name = "page") Integer page ,
                                                                        @Schema(description = "요청 페이지 사이즈", example = "3") @RequestParam(name = "page_size") Integer pageSize,
                                                                        Authentication authentication) {
        final Pageable pageable = PageRequest.of(page, pageSize);
        final User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(ResponseSuccess.success(feedService.getRoomFeeds(user, roomId, pageable)));
    }

    @Operation(summary = "전체 Feed 조회 API"
            , description = "인증에 성공한 도전 인증 피드 리스트들을 보여준다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - INVALID_REQUEST_FILED", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/feeds")
    public ResponseEntity<ResponseSuccess<Page<FeedResponse>>> getFeeds(@Schema(description = "요청 페이지 번호", example = "0") @RequestParam(name = "page") Integer page ,
                                                                        @Schema(description = "요청 페이지 사이즈", example = "3") @RequestParam(name = "page_size") Integer pageSize,
                                                                        Authentication authentication) {
        final Pageable pageable = PageRequest.of(page, pageSize);
        final User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(ResponseSuccess.success(feedService.getFeeds(user, pageable)));
    }

    @Operation(summary = "피드 좋아요 요청 API"
            , description = "인증에 성공한 도전 인증 피드 리스트들을 보여준다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - NOT_FOUND_FEED", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PostMapping("/like/{feed_id}")
    public ResponseEntity<ResponseSuccess<FeedResponse>> postFeedLike(@PathVariable(name = "feed_id") final Long feedId, final Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(ResponseSuccess.success(feedService.postFeedLike(feedId, user)));
    }

    @Operation(summary = "피드 좋아요 취소 요청 API"
            , description = "인증에 성공한 도전 인증 피드 리스트들을 보여준다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - NOT_FOUND_FEED", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @DeleteMapping("/like/{feed_id}")
    public ResponseEntity<ResponseSuccess<FeedResponse>> deleteFeedLike(@PathVariable(name = "feed_id") final Long feedId, final Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(ResponseSuccess.success(feedService.deleteFeedLike(feedId, user)));
    }

    @Operation(summary = "댓글 요청 API"
            , description = "피드에 댓글 생성을 한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - NOT_FOUND_FEED", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PostMapping("/comment/{feed_id}")
    public ResponseEntity<ResponseSuccess<?>> postFeedComment(@PathVariable(name = "feed_id") final Long feedId, @RequestBody final CommentCreateRequest commentCreateRequest, final Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(ResponseSuccess.success(feedService.postFeedComment(feedId, user, commentCreateRequest)));
    }
}
