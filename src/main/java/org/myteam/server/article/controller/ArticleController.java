package org.myteam.server.article.controller;

import lombok.RequiredArgsConstructor;
import org.myteam.server.article.controller.response.ArticleResponse;
import org.myteam.server.article.entity.Type;
import org.myteam.server.article.service.ArticleReadService;
import org.myteam.server.global.web.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleReadService articleReadService;

    @GetMapping("/list/{type}")
    private ResponseEntity<ResponseDto<List<ArticleResponse>>> findAll(@PathVariable Type type) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "기사 목록 조회 성공",
                articleReadService.findAll(type)));
    }

    @GetMapping("/{articleId}")
    private ResponseEntity<ResponseDto<ArticleResponse>> findById(@PathVariable Long articleId) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "기사 상세 조회 성공",
                articleReadService.findById(articleId)));
    }
}
