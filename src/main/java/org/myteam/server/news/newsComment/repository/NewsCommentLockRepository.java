package org.myteam.server.news.newsComment.repository;

import java.util.Optional;

import org.myteam.server.news.newsComment.domain.NewsComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

public interface NewsCommentLockRepository extends JpaRepository<NewsComment, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<NewsComment> findById(Long newsCommentId);
}
