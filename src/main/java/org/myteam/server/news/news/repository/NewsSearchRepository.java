package org.myteam.server.news.news.repository;

import org.myteam.server.news.news.domain.NewsDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface NewsSearchRepository extends ElasticsearchRepository<NewsDocument, String> {

    // 기본 쿼리: 제목 또는 내용에 키워드 포함
    Page<NewsDocument> findByTitleContainingOrContentContaining(
            String title, String content, Pageable pageable
    );
}
