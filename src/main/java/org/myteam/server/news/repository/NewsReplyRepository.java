package org.myteam.server.news.repository;

import org.myteam.server.news.domain.NewsReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsReplyRepository extends JpaRepository<NewsReply, Long> {
}
