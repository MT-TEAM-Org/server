package org.myteam.server.news.newsCount.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;

import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.news.newsCount.dto.service.response.NewsRecommendResponse;
import org.myteam.server.news.newsCount.service.NewsCountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/news/count")
@RequiredArgsConstructor
public class NewsCountController {

	private final NewsCountService newsCountService;

	@Operation(summary = "뉴스 추천수 추가 API", description = "뉴스를 추천합니다.")
	@PatchMapping("/recommend/{newsId}")
	public ResponseEntity<ResponseDto<NewsRecommendResponse>> recommend(
		@PathVariable
		@Parameter(description = "뉴스 ID")
		Long newsId
	) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 추천 추가 성공",
			newsCountService.recommendNews(newsId)));
	}

	@Operation(summary = "뉴스 추천수 삭제 API", description = "뉴스의 추천을 취소합니다.")
	@DeleteMapping("/recommend/{newsId}")
	public ResponseEntity<ResponseDto<NewsRecommendResponse>> cancelRecommend(
		@PathVariable
		@Parameter(description = "뉴스 ID")
		Long newsId
	) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 추천 삭제 성공",
			newsCountService.recommendNews(newsId)));
	}

}
