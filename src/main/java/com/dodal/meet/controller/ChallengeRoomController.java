package com.dodal.meet.controller;

import com.dodal.meet.controller.request.challengeroom.*;
import com.dodal.meet.controller.response.ResponseFail;
import com.dodal.meet.controller.response.ResponseSuccess;
import com.dodal.meet.controller.response.challengeroom.*;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.User;
import com.dodal.meet.service.ChallengeRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.net.URI;
import java.util.List;

@Tag(name = "ChallengeRoom", description = "도전방 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
@Validated
public class ChallengeRoomController {

    private final ChallengeRoomService challengeRoomService;


    @Operation(summary = "도전방 조회 API"
            , description = "홈 화면 - 0 : 관심있는 도전,  1 : 인기있는 도전, 2 : 최근 도전 정보를 반환한다. (관심있는 도전의 경우 조회 할 태그 값을 파라미터에 전달 필수) pageable의 경우 page : 0부터 시작, 조회할 개수 size를 입력한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "INVALID_ROOM_SEARCH_TYPE, NOT_FOUND_CATEGORY", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/challenge/rooms")
    public ResponseEntity<ResponseSuccess<Page<ChallengeRoomSearchResponse>>> getChallengeRoom(@Schema(description = "조회 조건 (0, 1, 2)", example = "0") @RequestParam(name = "condition") String condition,
                                                                                                     @Schema(description = "카테고리 코드 값 (관심있는 도전에서 사용)", example = "001") @RequestParam(name = "categoryValue", required = false) String categoryValue,
                                                                                                     @Schema(description = "요청 페이지 번호", example = "0") @RequestParam(name = "page") Integer page ,
                                                                                                     @Schema(description = "요청 페이지 사이즈", example = "3") @RequestParam(name = "page_size") Integer pageSize,
                                                                                                     Authentication authentication) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return ResponseEntity.ok().body(ResponseSuccess.success(challengeRoomService.getChallengeRooms(condition, categoryValue, pageable, authentication)));
    }

    @Operation(summary = "도전방 카테고리별 조회 API"
            , description = "홈 화면 - 전체 / 카테고리 / 태그 별 도전방 조회 정보를 반환한다.\n" +
            "카테고리 / 태그 값 정보가 없으면 전체 조회\n" +
            "condition_code : 0(인기순) , 1(최신순), 2(인원 많은 순), 3(인원 적은순)\n" +
            "cert_cnt_list : 빈도 선택 값 아무 선택이 없으면 전체 조회 그 외는 빈도 수 리스트 요청",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "INVALID_REQUEST_FIELD", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/challenge/rooms/category")
    public ResponseEntity<ResponseSuccess<Page<ChallengeRoomSearchResponse>>> getChallengeRoomByCategory(@Schema(description = "카테고리 코드 값", example = "001") @RequestParam(name="category_value",required = false) String categoryValue,
                                                                                                        @Schema(description = "태그 코드 값", example = "001001") @RequestParam(name = "tag_value", required = false) String tagValue,
                                                                                                        @Schema(description = "조회 코드 값 - 0(인기순) , 1(최신순), 2(인원 많은 순), 3(인원 적은순) ", allowableValues = {"0", "1", "2", "3"}, example = "0") @RequestParam(name = "condition_code", defaultValue = "0") String conditionCode,
                                                                                                        @ArraySchema(schema = @Schema(implementation = Integer.class, description = "도전 빈도 리스트", example = "[1, 3, 5]", type = "array")) @RequestParam(name = "cert_cnt_list") List<Integer> certCntList,
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
        return ResponseEntity.ok().body(ResponseSuccess.success(challengeRoomService.getChallengeRoomsByCategory(request, pageable, authentication)));
    }

    @Operation(summary = "도전방 상세 조회 API"
            , description = "도전방 상세 정보를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_ROOM", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/challenge/rooms/{room_id}")
    public ResponseEntity<ResponseSuccess<ChallengeRoomDetailResponse>> getChallengeRoomDetail(@PathVariable(name = "room_id") Integer roomId, Authentication authentication) {
        return ResponseEntity.ok().body(ResponseSuccess.success(challengeRoomService.getChallengeRoomDetail(roomId, authentication)));
    }

    @Operation(summary = "도전방 가입 API"
            , description = "도전방에 가입한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_ROOM", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PostMapping("/challenge/rooms/{room_id}/join")
    public ResponseEntity<ResponseSuccess<ChallengeRoomDetailResponse>> joinChallengeRoom(@PathVariable(name = "room_id") Integer roomId, Authentication authentication) {
        return ResponseEntity.ok().body(ResponseSuccess.success(challengeRoomService.joinChallengeRoom(roomId, authentication)));
    }

    @Operation(summary = "도전방 탈퇴 API"
            , description = "도전방에 탈퇴한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_ROOM", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @DeleteMapping("/challenge/rooms/{room_id}/join")
    public ResponseEntity<ResponseSuccess<Void>> leaveChallengeRoom(@PathVariable(name = "room_id") Integer roomId, Authentication authentication) {
        challengeRoomService.leaveChallengeRoom(roomId, authentication);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "도전방 생성 API"
            , description = "도전방을 생성한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - INVALID_REQUEST_FIELD", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PostMapping(value = "/challenge/room")
    public ResponseEntity<ResponseSuccess<ChallengeCreateResponse>> createChallengeRoom(@Valid @RequestBody ChallengeRoomCreateRequest request,  final Authentication authentication) {
        ChallengeCreateResponse result = challengeRoomService.createChallengeRoom(request, authentication);
        return ResponseEntity.created(URI.create("/challenge/room/" + result.getRoomId())).body(ResponseSuccess.success(result));
    }

    @Operation(summary = "도전방 수정 API"
            , description = "도전방을 수정한다. 도전방 생성 필드값 검증과 동일",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - INVALID_REQUEST_FIELD", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PatchMapping(value = "/challenge/room/{room_id}")
    public ResponseEntity<ResponseSuccess<ChallengeRoomDetailResponse>> updateChallengeRoom(@PathVariable(name = "room_id") Integer roomId, @RequestBody @Valid ChallengeRoomUpdateRequest request, final Authentication authentication) {
        return ResponseEntity.ok().body(ResponseSuccess.success(challengeRoomService.updateChallengeRoom(roomId, request, authentication)));
    }

    @Operation(summary = "북마크 리스트 조회 API"
            , description = "북마크 등록한 도전방 리스트를 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/challenge/room/bookmarks")
    public ResponseEntity<ResponseSuccess<List<ChallengeRoomBookmarkResponse>>> getBookmarksByUser(Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(ResponseSuccess.success(challengeRoomService.getBookmarksByUser(user)));
    }

    @Operation(summary = "북마크 등록 API"
            , description = "도전방에 북마크를 등록한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "BOOKMARK_ALREADY_EXIST", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PostMapping("/challenge/room/{room_id}/bookmark")
    public ResponseEntity<ResponseSuccess<Void>> createBookmark(@PathVariable(name = "room_id") Integer roomId, Authentication authentication) {
        challengeRoomService.createBookmark(roomId, authentication);
        return ResponseEntity.created(URI.create("/challenge/room/" + roomId +"/bookmark")).body(ResponseSuccess.success());
    }


    @Operation(summary = "북마크 삭제 API"
            , description = "도전방에 북마크를 삭제한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_BOOKMARK", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @DeleteMapping("/challenge/room/{room_id}/bookmark")
    public ResponseEntity<ResponseSuccess<Void>> deleteBookmark(@PathVariable(name = "room_id") Integer roomId, Authentication authentication) {
        challengeRoomService.deleteBookmark(roomId, authentication);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "도전방 인증 API"
            , description = "도전방에 이미지 / 인증글을 통해 인증한다. (인증글은 100자 이내)",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "BOOKMARK_ALREADY_EXIST", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PostMapping(value = "/challenge/room/{room_id}/certification")
    public ResponseEntity<ResponseSuccess<Void>> createCertification(@PathVariable(name = "room_id") Integer roomId, @Valid @RequestBody ChallengeFeedCreateRequest request, Authentication authentication) {
        challengeRoomService.createCertification(roomId, request, authentication);
        return ResponseEntity.created(URI.create("/challenge/room/" + roomId +"/certification")).body(ResponseSuccess.success());
    }

    @Operation(summary = "공지사항 등록 API"
            , description = "도전방에 공지사항을 등록한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_ROOM", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN, UNAUTHORIZED_ROOM_HOST", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PostMapping("/challenge/room/{room_id}/noti")
    public ResponseEntity<ResponseSuccess<Void>> registNoti(@PathVariable(name = "room_id") Integer roomId, @RequestBody ChallengeNotiCreateRequest challengeNotiCreateRequest, Authentication authentication) {
        challengeRoomService.registNoti(roomId, challengeNotiCreateRequest, authentication);
        return ResponseEntity.created(URI.create("/challenge/room/" + roomId + "/noti")).body(ResponseSuccess.success());
    }

    @Operation(summary = "공지사항 조회 API"
            , description = "도전방에 공지사항을 등록한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_ROOM", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN, UNAUTHORIZED_ROOM_HOST", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/challenge/room/{room_id}/noti")
    public ResponseEntity<ResponseSuccess<List<ChallengeNotiResponse>>> getNotis(@PathVariable(name = "room_id") Integer roomId, Authentication authentication) {
        return ResponseEntity.ok().body(ResponseSuccess.success(challengeRoomService.getNotis(roomId, authentication)));
    }

    @Operation(summary = "공지사항 수정 API"
            , description = "도전방에 공지사항을 수정한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_ROOM", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN, UNAUTHORIZED_ROOM_HOST", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PatchMapping("/challenge/room/{room_id}/noti/{noti_id}")
    public ResponseEntity<ResponseSuccess<Void>> updateNoti(@PathVariable(name = "room_id") Integer roomId, @PathVariable(name = "noti_id") Integer notiId, @RequestBody ChallengeNotiUpdateRequest challengeNotiUpdateRequest, Authentication authentication) {
        challengeRoomService.updateNoti(roomId, notiId, challengeNotiUpdateRequest, authentication);
        return ResponseEntity.ok().body(ResponseSuccess.success());
    }

    @Operation(summary = "공지사항 삭제 API"
            , description = "도전방 공지사항을 삭제한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_ROOM", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN, UNAUTHORIZED_ROOM_HOST", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @DeleteMapping("/challenge/room/{room_id}/noti/{noti_id}")
    public ResponseEntity<ResponseSuccess<Void>> deleteNoti(@PathVariable(name = "room_id") Integer roomId, @PathVariable(name = "noti_id") Integer notiId, Authentication authentication) {
        challengeRoomService.deleteNoti(roomId, notiId, authentication);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "랭킹 조회 API"
            , description = "도전방에 랭킹 정보를 조회한다. code : 0 (전체), 1(월간), 2(주간)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "INVALID_CODE", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/challenge/room/{room_id}/rank")
    public ResponseEntity<ResponseSuccess<List<ChallengeRoomRankResponse>>> getRank(@PathVariable(name = "room_id") Integer roomId, @RequestParam(name = "code") String code, Authentication authentication) {
        if (!code.equals("0") && !code.equals("1") && !code.equals("2")) {
            throw new DodalApplicationException(ErrorCode.INVALID_RANK_CODE);
        }
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(ResponseSuccess.success(challengeRoomService.getRank(roomId, code, user)));
    }

    @Operation(summary = "도전방 검색 API"
            , description = "검색 단어에 맞는 도전방을 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "INVALID_REQUEST", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/challenge/room/search")
    public ResponseEntity<ResponseSuccess<List<ChallengeRoomSearchResponse>>> getSearch(@RequestParam(name = "word") String word, Authentication authentication) {
        if (!StringUtils.hasText(word)) {
            throw new DodalApplicationException(ErrorCode.NOT_FOUND_WORD);
        }
        final String searchWord = word.trim();
        final User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(ResponseSuccess.success(challengeRoomService.getChallengeRoomsByWord(user, searchWord)));
    }

    @Operation(summary = "도전방 최근 검색 API"
            , description = "유저가 검색한 최근 검색어를 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "INVALID_REQUEST", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/challenge/room/search/words")
    public ResponseEntity<ResponseSuccess<List<String>>> getWordsByUserId(Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(ResponseSuccess.success(challengeRoomService.getChallengeWordsByUserId(user)));
    }

    @Operation(summary = "도전방 검색기록 삭제 API"
            , description = "유저가 검색한 검색어를 모두 삭제한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @DeleteMapping("/challenge/room/search/words")
    public ResponseEntity<ResponseSuccess<Void>> deleteWordsByUserId(Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        challengeRoomService.deleteChallengeWordsByUserId(user);
        return ResponseEntity.noContent().build();
    }
}
