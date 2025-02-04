package org.myteam.server.news.newsCount.repository;

import java.util.Optional;

import org.myteam.server.news.newsCount.domain.NewsCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.LockModeType;

@Repository
public interface NewsCountRepository extends JpaRepository<NewsCount, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<NewsCount> findByNewsId(Long newsId);
}
