package org.myteam.server.news.newsReply.repository;

import org.myteam.server.news.newsReply.domain.NewsReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsReplyRepository extends JpaRepository<NewsReply, Long> {
}
