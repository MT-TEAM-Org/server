package org.myteam.server.news.newsComment.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;

import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.news.newsComment.dto.controller.request.NewsCommentRequest;
import org.myteam.server.news.newsComment.dto.controller.request.NewsCommentSaveRequest;
import org.myteam.server.news.newsComment.dto.controller.request.NewsCommentUpdateRequest;
import org.myteam.server.news.newsComment.dto.service.response.NewsCommentListResponse;
import org.myteam.server.news.newsComment.dto.service.response.NewsCommentResponse;
import org.myteam.server.news.newsComment.service.NewsCommentReadService;
import org.myteam.server.news.newsComment.service.NewsCommentService;
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
@RequestMapping("/api/news/comment")
public class NewsCommentController {

	private final NewsCommentService newsCommentService;
	private final NewsCommentReadService newsCommentReadService;

	@Operation(summary = "뉴스 댓글 저장 API", description = "뉴스 댓글을 저장합니다.")
	@PostMapping
	public ResponseEntity<ResponseDto<NewsCommentResponse>> save(
		@RequestBody @Valid NewsCommentSaveRequest newsSaveRequest, HttpServletRequest request) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 댓글 저장 성공",
			newsCommentService.save(newsSaveRequest.toServiceRequest(ClientUtils.getRemoteIP(request)))));
	}

	@Operation(summary = "뉴스 댓글 목록 조회 API", description = "뉴스 댓글을 조회합니다.")
	@GetMapping
	public ResponseEntity<ResponseDto<NewsCommentListResponse>> findByNewsId(
		@ModelAttribute @Valid NewsCommentRequest newsCommentRequest) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 댓글 조회 성공",
			newsCommentReadService.findByNewsId(newsCommentRequest.toServiceRequest())));
	}

	@Operation(summary = "뉴스 댓글 수정 API", description = "뉴스 댓글을 수정합니다.")
	@PatchMapping
	public ResponseEntity<ResponseDto<Long>> update(
		@RequestBody @Valid NewsCommentUpdateRequest newsCommentUpdateRequest) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 댓글 수정 성공",
			newsCommentService.update(newsCommentUpdateRequest.toServiceRequest())));
	}

	@Operation(summary = "뉴스 댓글 삭제 API", description = "뉴스 댓글을 삭제합니다.")
	@DeleteMapping("/{newsCommentId}")
	public ResponseEntity<ResponseDto<Long>> delete(
		@PathVariable
		@Parameter(description = "뉴스 댓글 ID")
		Long newsCommentId
	) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 댓글 삭제 성공",
			newsCommentService.delete(newsCommentId)));
	}

	@Operation(summary = "뉴스 댓글 추천 API", description = "뉴스 댓글을 추천합니다.")
	@PatchMapping("/recommend/{newsCommentId}")
	public ResponseEntity<ResponseDto<Long>> recommend(
		@PathVariable
		@Parameter(description = "뉴스 댓글 ID")
		Long newsCommentId
	) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 댓글 추천 성공",
			newsCommentService.recommend(newsCommentId)));
	}

	@Operation(summary = "뉴스 댓글 추천 취소 API", description = "뉴스 댓글을 추천 취소합니다.")
	@DeleteMapping("/recommend/{newsCommentId}")
	public ResponseEntity<ResponseDto<Long>> cancelRecommend(
		@PathVariable
		@Parameter(description = "뉴스 댓글 ID")
		Long newsCommentId
	) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 댓글 추천 삭제 성공",
			newsCommentService.cancelRecommend(newsCommentId)));
	}
}
