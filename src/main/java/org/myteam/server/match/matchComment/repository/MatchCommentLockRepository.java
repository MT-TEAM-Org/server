package org.myteam.server.match.matchComment.repository;

import java.util.Optional;

import org.myteam.server.match.matchComment.domain.MatchComment;
import org.myteam.server.news.newsCount.domain.NewsCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

@Repository
public interface MatchCommentLockRepository extends JpaRepository<MatchComment, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<MatchComment> findById(Long matchCommentId);
}
