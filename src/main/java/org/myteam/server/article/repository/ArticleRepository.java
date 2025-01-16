package org.myteam.server.article.repository;

import org.myteam.server.article.entity.Article;
import org.myteam.server.article.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByType(Type type);
}
