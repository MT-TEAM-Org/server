package org.myteam.server.mypage.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.dto.reponse.BoardListResponse;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.inquiry.dto.request.InquiryRequest.*;
import org.myteam.server.inquiry.dto.response.InquiryResponse.*;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.inquiry.service.InquiryService;
import org.myteam.server.mypage.dto.request.MyBoardSearchRequest;
import org.myteam.server.mypage.dto.request.MyPageRequest.MyPageUpdateRequest;
import org.myteam.server.mypage.dto.response.MyPageResponse.MemberModifyResponse;
import org.myteam.server.mypage.dto.response.MyPageResponse.MemberStatsResponse;
import org.myteam.server.mypage.service.MyPageReadService;
import org.myteam.server.mypage.service.MyPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my-page")
@Tag(name = "마이페이지 API", description = "마이페이지 관련 API")
public class MyPageController {

    private final MyPageReadService myPageReadService;
    private final MyPageService myPageService;
    private final InquiryReadService inquiryReadService;
    private final InquiryService inquiryService;

    /**
     * 마이페이지 회원 정보 보여주기
     *
     * @return
     */
    @Operation(summary = "마이페이지 회원 정보 조회", description = "로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ResponseDto<MemberStatsResponse>> getMyPage() {
        MemberStatsResponse response = myPageReadService.getMemberInfo();

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "회원 정보가 조회되었습니다.",
                response
        ));
    }

    /**
     * 마이페이지 회원 정보 수정 목록 보여주기
     *
     * @return
     */
    @Operation(summary = "회원 정보 수정 페이지 조회", description = "회원 정보 수정 시 필요한 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/modify")
    public ResponseEntity<ResponseDto<MemberModifyResponse>> getMyInfo() {
        MemberModifyResponse response = myPageReadService.getMemberAllInfo();

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "내 정보 수정 조회내역입니다.",
                response
        ));
    }

    /**
     * 마이페이지 회원 정보 수정
     *
     * @param myPageUpdateRequest
     * @return
     */
    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "본인이나 관리자가 아님.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/modify")
    public ResponseEntity<ResponseDto<String>> updateMyInfo(
            @RequestBody MyPageUpdateRequest myPageUpdateRequest
    ) {
        myPageService.updateMemberInfo(myPageUpdateRequest);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "회원 정보가 수정되었습니다.",
                null
        ));
    }

    /**
     * 내가 쓴 게시글
     *
     * @param myBoardSearchRequest
     * @return
     */
    @Operation(summary = "내가 작성한 게시글 조회", description = "사용자가 작성한 게시글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/board")
    public ResponseEntity<ResponseDto<BoardListResponse>> getMyBoard(
            @Valid @ModelAttribute MyBoardSearchRequest myBoardSearchRequest
    ) {
        BoardListResponse memberPosts = myPageReadService.getMemberPosts(myBoardSearchRequest.toServiceRequest());

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "내가 쓴 게시물이 조회되었습니다.",
                memberPosts
        ));
    }

    /**
     * @TODO: 내가 쓴 댓글
     */


    /**
     * 문의 내역 목록 조회
     *
     * @param request
     * @return
     */
    @Operation(summary = "내가 작성한 문의 내역 조회", description = "사용자가 작성한 문의 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/inquiry")
    public ResponseEntity<ResponseDto<InquiriesListResponse>> getMyInquiry(
            @Valid @ModelAttribute InquirySearchRequest request
    ) {
        InquiriesListResponse inquiriesListResponse = inquiryReadService.getInquiriesByMember(request.toServiceRequest());

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "내가 쓴 문의내역이 조회되었습니다.",
                inquiriesListResponse
        ));
    }

    /**
     * 문의내역 삭제
     */
    @Operation(summary = "문의 내역 삭제", description = "사용자가 작성한 문의를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "문의 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })

    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<ResponseDto<Void>> deleteBoard(@PathVariable final Long inquiryId) {
        inquiryService.deleteInquiry(inquiryId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "문의내역 삭제 성공",
                null
        ));
    }

    /**
     * 문의내역 상세 조회
     */
    @Operation(summary = "문의 내역 상세 조회", description = "사용자가 작성한 특정 문의 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "문의 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{inquiryId}")
    public ResponseEntity<ResponseDto<InquiryDetailsResponse>> getBoard(@PathVariable final Long inquiryId) {
        final InquiryDetailsResponse response = inquiryReadService.getInquiryById(inquiryId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "문의 내역 조회 성공",
                response
        ));
    }
}
