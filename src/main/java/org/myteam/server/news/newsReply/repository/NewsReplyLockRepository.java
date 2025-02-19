package org.myteam.server.news.newsReply.repository;

import java.util.Optional;

import org.myteam.server.news.newsComment.domain.NewsComment;
import org.myteam.server.news.newsReply.domain.NewsReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

public interface NewsReplyLockRepository extends JpaRepository<NewsReply, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<NewsReply> findById(Long newsReplyId);
}
