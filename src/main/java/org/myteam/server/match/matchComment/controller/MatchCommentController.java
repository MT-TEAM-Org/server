package org.myteam.server.match.matchComment.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;

import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.match.matchComment.dto.controller.request.MatchCommentRequest;
import org.myteam.server.match.matchComment.dto.controller.request.MatchCommentSaveRequest;
import org.myteam.server.match.matchComment.dto.controller.request.MatchCommentUpdateRequest;
import org.myteam.server.match.matchComment.dto.service.response.MatchCommentListResponse;
import org.myteam.server.match.matchComment.dto.service.response.MatchCommentResponse;
import org.myteam.server.match.matchComment.service.MatchCommentReadService;
import org.myteam.server.match.matchComment.service.MatchCommentService;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match/comment")
public class MatchCommentController {

	private final MatchCommentService matchCommentService;
	private final MatchCommentReadService matchCommentReadService;

	@Operation(summary = "경기 댓글 저장 API", description = "경기 댓글을 저장합니다.")
	@PostMapping
	public ResponseEntity<ResponseDto<MatchCommentResponse>> save(
		@RequestBody @Valid MatchCommentSaveRequest matchCommentSaveRequest, HttpServletRequest request) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"경기 댓글 저장 성공",
			matchCommentService.save(matchCommentSaveRequest.toServiceRequest(ClientUtils.getRemoteIP(request)))));
	}

	@Operation(summary = "경기 댓글 목록 조회 API", description = "경기 댓글을 조회합니다.")
	@GetMapping
	public ResponseEntity<ResponseDto<MatchCommentListResponse>> findByNewsId(
		@ModelAttribute @Valid MatchCommentRequest matchCommentRequest) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"경기 댓글 조회 성공",
			matchCommentReadService.findByMatchId(matchCommentRequest.toServiceRequest())));
	}

	@Operation(summary = "경기 댓글 수정 API", description = "경기 댓글을 수정합니다.")
	@PatchMapping
	public ResponseEntity<ResponseDto<Long>> update(
		@RequestBody @Valid MatchCommentUpdateRequest matchCommentUpdateRequest) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"경기 댓글 수정 성공",
			matchCommentService.update(matchCommentUpdateRequest.toServiceRequest())));
	}

	@Operation(summary = "경기 댓글 삭제 API", description = "경기 댓글을 삭제합니다.")
	@DeleteMapping("/{matchCommentId}")
	public ResponseEntity<ResponseDto<Long>> delete(
		@PathVariable
		@Parameter(description = "경기 댓글 ID")
		Long matchCommentId
	) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"경기 댓글 삭제 성공",
			matchCommentService.delete(matchCommentId)));
	}
}
