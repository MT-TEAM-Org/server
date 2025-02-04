package org.myteam.server.news.newsCount.repository;

import java.util.Optional;

import org.myteam.server.news.newsCount.domain.NewsCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsCountRepository extends JpaRepository<NewsCount, Long> {
	Optional<NewsCount> findByNewsId(Long newsId);
}
