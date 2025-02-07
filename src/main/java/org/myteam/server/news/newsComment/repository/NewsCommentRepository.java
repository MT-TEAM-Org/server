package org.myteam.server.news.newsComment.repository;

import org.myteam.server.news.newsComment.domain.NewsComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsCommentRepository extends JpaRepository<NewsComment, Long> {
}
