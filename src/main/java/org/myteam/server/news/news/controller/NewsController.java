package org.myteam.server.news.news.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.news.news.dto.controller.request.NewsRequest;
import org.myteam.server.news.news.dto.service.response.NewsListResponse;
import org.myteam.server.news.news.dto.service.response.NewsResponse;
import org.myteam.server.news.news.service.NewsReadService;
import org.myteam.server.news.newsCount.service.NewsCountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsReadService newsReadService;

    @Operation(summary = "뉴스 목록 조회 API", description = "뉴스의 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ResponseDto<NewsListResponse>> findAll(@ModelAttribute @Valid NewsRequest request) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "뉴스 목록 조회 성공",
                newsReadService.findAll(request.toServiceRequest())));
    }

    @Operation(summary = "뉴스 상세 조회 API", description = "뉴스의 상세를 조회합니다.")
    @GetMapping("/{newsId}")
    public ResponseEntity<ResponseDto<NewsResponse>> findOne(
            @PathVariable
            @Parameter(description = "뉴스 ID")
            Long newsId
    ) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "뉴스 상세 조회 성공",
                newsReadService.findOne(newsId)));
    }

}
