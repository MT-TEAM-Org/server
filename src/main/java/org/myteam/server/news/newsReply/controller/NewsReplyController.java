package org.myteam.server.news.newsReply.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;

import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.news.newsReply.dto.controller.request.NewsReplyRequest;
import org.myteam.server.news.newsReply.dto.controller.request.NewsReplySaveRequest;
import org.myteam.server.news.newsReply.dto.controller.request.NewsReplyUpdateRequest;
import org.myteam.server.news.newsReply.dto.service.response.NewsReplyListResponse;
import org.myteam.server.news.newsReply.dto.service.response.NewsReplyResponse;
import org.myteam.server.news.newsReply.service.NewsReplyReadService;
import org.myteam.server.news.newsReply.service.NewsReplyService;
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
@RequestMapping("/api/news/reply")
public class NewsReplyController {

	private final NewsReplyService newsReplyService;
	private final NewsReplyReadService newsReplyReadService;

	@Operation(summary = "뉴스 대댓글 저장 API", description = "뉴스 댓글의 대댓글을 추가합니다.")
	@PostMapping
	public ResponseEntity<ResponseDto<NewsReplyResponse>> save(
		@RequestBody @Valid NewsReplySaveRequest newsSaveRequest, HttpServletRequest request) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 대댓글 저장 성공",
			newsReplyService.save(newsSaveRequest.toServiceRequest(ClientUtils.getRemoteIP(request)))));
	}

	@Operation(summary = "뉴스 대댓글 목록 조회 API", description = "뉴스 댓글의 대댓글 목록을 조회합니다.")
	@GetMapping
	public ResponseEntity<ResponseDto<NewsReplyListResponse>> findByNewsCommentId(
		@ModelAttribute @Valid NewsReplyRequest newsReplyRequest) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 대댓글 조회 성공",
			newsReplyReadService.findByNewsCommentId(newsReplyRequest.toServiceRequest())));
	}

	@Operation(summary = "뉴스 대댓글 수정 API", description = "뉴스 댓글의 대댓글을 수정합니다.")
	@PatchMapping
	public ResponseEntity<ResponseDto<Long>> update(
		@RequestBody @Valid NewsReplyUpdateRequest newsReplyUpdateRequest) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 대댓글 수정 성공",
			newsReplyService.update(newsReplyUpdateRequest.toServiceRequest())));
	}

	@Operation(summary = "뉴스 대댓글 삭제 API", description = "뉴스 댓글의 대댓글을 삭제합니다.")
	@DeleteMapping("/{newsReplyId}")
	public ResponseEntity<ResponseDto<Long>> delete(
		@PathVariable
		@Parameter(description = "뉴스 대댓글 ID")
		Long newsReplyId
	) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 대댓글 삭제 성공",
			newsReplyService.delete(newsReplyId)));
	}

	@Operation(summary = "뉴스 대댓글 추천 API", description = "뉴스 대댓글을 추천합니다.")
	@PatchMapping("/recommend/{newsReplyId}")
	public ResponseEntity<ResponseDto<Long>> recommend(
		@PathVariable
		@Parameter(description = "뉴스 댓글 ID")
		Long newsReplyId
	) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 대댓글 추천 성공",
			newsReplyService.recommend(newsReplyId)));
	}

	@Operation(summary = "뉴스 대댓글 추천 취소 API", description = "뉴스 대댓글을 추천 취소합니다.")
	@DeleteMapping("/recommend/{newsReplyId}")
	public ResponseEntity<ResponseDto<Long>> cancelRecommend(
		@PathVariable
		@Parameter(description = "뉴스 대댓글 ID")
		Long newsReplyId
	) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 대댓글 추천 삭제 성공",
			newsReplyService.cancelRecommend(newsReplyId)));
	}
}
