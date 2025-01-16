package org.myteam.server.article.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.article.controller.response.ArticleResponse;
import org.myteam.server.article.entity.Type;
import org.myteam.server.article.repository.ArticleRepository;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.myteam.server.global.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleReadService {

    private final ArticleRepository articleRepository;

    public List<ArticleResponse> findAll(Type type) {
        return articleRepository.findByType(type).stream()
                .map(ArticleResponse::of)
                .toList();
    }

    public ArticleResponse findById(Long id) {
        return ArticleResponse.of(articleRepository.findById(id)
                .orElseThrow(() -> new PlayHiveException(RESOURCE_NOT_FOUND, id + " 는 존재하지 않는 기사입니다.")));
    }

}
