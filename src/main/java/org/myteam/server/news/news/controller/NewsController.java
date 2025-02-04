package org.myteam.server.news.news.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;

import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.news.news.dto.controller.request.NewsRequest;
import org.myteam.server.news.news.dto.service.response.NewsListResponse;
import org.myteam.server.news.news.dto.service.response.NewsResponse;
import org.myteam.server.news.news.service.NewsReadService;
import org.myteam.server.news.newsCount.dto.service.response.NewsRecommendResponse;
import org.myteam.server.news.newsCount.service.NewsCountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

	private final NewsReadService newsReadService;

	@GetMapping
	public ResponseEntity<ResponseDto<NewsListResponse>> findAll(@RequestBody @Valid NewsRequest request) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 목록 조회 성공",
			newsReadService.findAll(request.toServiceRequest())));
	}

	@GetMapping("/{newsId}")
	public ResponseEntity<ResponseDto<NewsResponse>> findOne(@PathVariable Long newsId) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 상세 조회 성공",
			newsReadService.findOne(newsId)));
	}

}
