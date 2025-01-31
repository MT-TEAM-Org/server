package org.myteam.server.news.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;

import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.news.dto.controller.request.NewsRequest;
import org.myteam.server.news.dto.service.response.NewsListResponse;
import org.myteam.server.news.service.NewsReadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
	private ResponseEntity<ResponseDto<NewsListResponse>> findAll(@RequestBody @Valid NewsRequest request) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"뉴스 목록 조회 성공",
			newsReadService.findAll(request.toServiceRequest())));
	}

}
