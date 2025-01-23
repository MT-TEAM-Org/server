package org.myteam.server.article.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.myteam.server.article.controller.response.ArticleSaveResponse;
import org.myteam.server.article.dto.ArticleSaveRequest;
import org.myteam.server.article.repository.ArticleRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleSaveResponse save(ArticleSaveRequest articleSaveRequest) {
        return ArticleSaveResponse.of(articleRepository.save(articleSaveRequest.toEntity()));
    }

}
