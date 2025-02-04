package org.myteam.server.news.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;

import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.news.dto.controller.request.NewsCommentRequest;
import org.myteam.server.news.dto.controller.request.NewsReplyRequest;
import org.myteam.server.news.dto.controller.request.NewsReplySaveRequest;
import org.myteam.server.news.dto.controller.request.NewsReplyUpdateRequest;
import org.myteam.server.news.dto.service.response.NewsReplyListResponse;
import org.myteam.server.news.dto.service.response.NewsReplyResponse;
import org.myteam.server.news.service.NewsReplyReadService;
import org.myteam.server.news.service.NewsReplyService;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/news/reply")
public class NewsReplyController {

	private final NewsReplyService newsReplyService;
	private final NewsReplyReadService newsReplyReadService;

	@PostMapping
	private ResponseEntity<ResponseDto<NewsReplyResponse>> save(
		@RequestBody @Valid NewsReplySaveRequest newsSaveRequest, HttpServletRequest request) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 대댓글 저장 성공",
			newsReplyService.save(newsSaveRequest.toServiceRequest(ClientUtils.getRemoteIP(request)))));
	}

	@GetMapping
	private ResponseEntity<ResponseDto<NewsReplyListResponse>> findByNewsId(
		@RequestBody @Valid NewsReplyRequest newsReplyRequest) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 대댓글 조회 성공",
			newsReplyReadService.findByNewsCommentId(newsReplyRequest.toServiceRequest())));
	}

	@PatchMapping
	private ResponseEntity<ResponseDto<Long>> update(
		@RequestBody @Valid NewsReplyUpdateRequest newsReplyUpdateRequest) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 대댓글 수정 성공",
			newsReplyService.update(newsReplyUpdateRequest.toServiceRequest())));
	}

	@DeleteMapping("/{newsReplyId}")
	private ResponseEntity<ResponseDto<Long>> delete(@PathVariable Long newsReplyId) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 대댓글 삭제 성공",
			newsReplyService.delete(newsReplyId)));
	}
}
