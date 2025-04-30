package org.myteam.server.comment.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.myteam.server.comment.dto.request.CommentRequest.CommentDeleteRequest;
import org.myteam.server.comment.dto.request.CommentRequest.CommentListRequest;
import org.myteam.server.comment.dto.request.CommentRequest.CommentSaveRequest;
import org.myteam.server.comment.dto.response.CommentResponse.BestCommentSaveListResponse;
import org.myteam.server.comment.dto.response.CommentResponse.CommentSaveListResponse;
import org.myteam.server.comment.dto.response.CommentResponse.CommentSaveResponse;
import org.myteam.server.comment.service.CommentReadService;
import org.myteam.server.comment.service.CommentService;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "댓글 CRUD API", description = "댓글 생성, 수정, 상세 조회, 삭제 API")
public class CommentController {

    private final CommentService commentService;
    private final CommentReadService commentReadService;

    /**
     * 댓글 작성 API
     */
    @Operation(summary = "댓글 생성", description = "댓글을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "회원, 게시글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{contentId}/comment")
    public ResponseEntity<ResponseDto<CommentSaveResponse>> addComment(@PathVariable Long contentId,
                                                                       @Valid @RequestBody CommentSaveRequest request,
                                                                       HttpServletRequest httpServletRequest) {

        CommentSaveResponse response = commentService.addComment(contentId, request,
                ClientUtils.getRemoteIP(httpServletRequest));
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "댓글이 생성되었습니다.",
                response
        ));
    }

    /**
     * 댓글 수정 API
     */
    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자나 관리자만 수정 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "회원, 댓글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseDto<CommentSaveResponse>> updateComment(@PathVariable Long commentId,
                                                                          @Valid @RequestBody CommentSaveRequest request) {

        CommentSaveResponse response = commentService.update(commentId, request);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "댓글이 수정되었습니다.",
                response
        ));
    }

    /**
     * 댓글 삭제 API
     */
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자나 관리자만 삭제 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "회원, 게시글 or 댓글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "s3 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{contentId}/comment/{commentId}")
    public ResponseEntity<ResponseDto<Void>> deleteComment(@PathVariable Long contentId,
                                                           @PathVariable Long commentId,
                                                           @Valid @ModelAttribute CommentDeleteRequest request) {

        commentService.deleteComment(contentId, commentId, request);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "댓글이 삭제되었습니다.",
                null
        ));
    }

    /**
     * 댓글 목록 조회 API
     */
    @Operation(summary = "댓글 목록 조회", description = "댓글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{contentId}")
    public ResponseEntity<ResponseDto<CommentSaveListResponse>> getComments(@PathVariable Long contentId,
                                                                            @Valid @ModelAttribute CommentListRequest request) {
        CommentSaveListResponse response = commentReadService.getComments(contentId, request);

        return ResponseEntity.ok(new ResponseDto(
                SUCCESS.name(),
                "댓글 목록 조회 성공",
                response
        ));
    }

    /**
     * 댓글 상세 조회
     */
    @Operation(summary = "댓글 상세 조회", description = "댓글을 상세 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "댓글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{commentId}/detail")
    public ResponseEntity<ResponseDto<CommentSaveResponse>> getCommentDetail(@PathVariable Long commentId) {
        CommentSaveResponse comment = commentReadService.getCommentDetail(commentId);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "댓글 상세 조회 성공",
                comment
        ));
    }

    /**
     * 베스트 댓글 목록 조회
     */
    @Operation(summary = "베스트 댓글 목록 조회", description = "베스트 댓글을 목록 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "베스트 댓글 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{contentId}/best")
    public ResponseEntity<ResponseDto<BestCommentSaveListResponse>> getBestComments(@PathVariable Long contentId,
                                                                                    @Valid @ModelAttribute CommentListRequest request) {
        BestCommentSaveListResponse bestComments = commentReadService.getBestComments(contentId, request);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "베스트 댓글 목록 조회 성공",
                bestComments
        ));
    }
}
