package org.myteam.server.match.matchReply.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;

import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.match.matchReply.dto.controller.request.MatchReplyRequest;
import org.myteam.server.match.matchReply.dto.controller.request.MatchReplySaveRequest;
import org.myteam.server.match.matchReply.dto.controller.request.MatchReplyUpdateRequest;
import org.myteam.server.match.matchReply.dto.service.response.MatchReplyListResponse;
import org.myteam.server.match.matchReply.dto.service.response.MatchReplyResponse;
import org.myteam.server.match.matchReply.service.MatchReplyReadService;
import org.myteam.server.match.matchReply.service.MatchReplyService;
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
@RequestMapping("/api/match/reply")
public class MatchReplyController {

	private final MatchReplyService matchReplyService;
	private final MatchReplyReadService matchReplyReadService;

	@Operation(summary = "경기 대댓글 저장 API", description = "경기 댓글의 대댓글을 추가합니다.")
	@PostMapping
	public ResponseEntity<ResponseDto<MatchReplyResponse>> save(
		@RequestBody @Valid MatchReplySaveRequest matchReplySaveRequest, HttpServletRequest request) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"경기 대댓글 저장 성공",
			matchReplyService.save(matchReplySaveRequest.toServiceRequest(ClientUtils.getRemoteIP(request)))));
	}

	@Operation(summary = "경기 대댓글 목록 조회 API", description = "경기 댓글의 대댓글 목록을 조회합니다.")
	@GetMapping
	public ResponseEntity<ResponseDto<MatchReplyListResponse>> findByNewsCommentId(
		@ModelAttribute @Valid MatchReplyRequest matchReplyRequest) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"경기 대댓글 조회 성공",
			matchReplyReadService.findByMatchCommentId(matchReplyRequest.toServiceRequest())));
	}

	@Operation(summary = "경기 대댓글 수정 API", description = "경기 댓글의 대댓글을 수정합니다.")
	@PatchMapping
	public ResponseEntity<ResponseDto<Long>> update(
		@RequestBody @Valid MatchReplyUpdateRequest matchReplyUpdateRequest) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"경기 대댓글 수정 성공",
			matchReplyService.update(matchReplyUpdateRequest.toServiceRequest())));
	}

	@Operation(summary = "경기 대댓글 삭제 API", description = "경기 댓글의 대댓글을 삭제합니다.")
	@DeleteMapping("/{matchReplyId}")
	public ResponseEntity<ResponseDto<Long>> delete(
		@PathVariable
		@Parameter(description = "경기 대댓글 ID")
		Long matchReplyId
	) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"경기 대댓글 삭제 성공",
			matchReplyService.delete(matchReplyId)));
	}
}
