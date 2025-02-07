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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news/comment")
public class NewsCommentController {

	private final NewsCommentService newsCommentService;
	private final NewsCommentReadService newsCommentReadService;

	@PostMapping
	public ResponseEntity<ResponseDto<NewsCommentResponse>> save(@RequestBody @Valid NewsCommentSaveRequest newsSaveRequest, HttpServletRequest request) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 댓글 저장 성공",
			newsCommentService.save(newsSaveRequest.toServiceRequest(ClientUtils.getRemoteIP(request)))));
	}

	@GetMapping
	public ResponseEntity<ResponseDto<NewsCommentListResponse>> findByNewsId(
		@ModelAttribute @Valid NewsCommentRequest newsCommentRequest) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 댓글 조회 성공",
			newsCommentReadService.findByNewsId(newsCommentRequest.toServiceRequest())));
	}

	@PatchMapping
	public ResponseEntity<ResponseDto<Long>> update(
		@RequestBody @Valid NewsCommentUpdateRequest newsCommentUpdateRequest) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 댓글 수정 성공",
			newsCommentService.update(newsCommentUpdateRequest.toServiceRequest())));
	}

	@DeleteMapping("/{newsCommentId}")
	public ResponseEntity<ResponseDto<Long>> delete(@PathVariable Long newsCommentId) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 댓글 삭제 성공",
			newsCommentService.delete(newsCommentId)));
	}
}
