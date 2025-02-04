package org.myteam.server.news.newsCount.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;

import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.news.newsCount.dto.service.response.NewsRecommendResponse;
import org.myteam.server.news.newsCount.service.NewsCountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/news/count")
@RequiredArgsConstructor
public class NewsCountController {

	private final NewsCountService newsCountService;

	@PatchMapping("/recommend/{newsId}")
	public ResponseEntity<ResponseDto<NewsRecommendResponse>> like(@PathVariable Long newsId) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 추천 추가 성공",
			newsCountService.recommendNews(newsId)));
	}

}
