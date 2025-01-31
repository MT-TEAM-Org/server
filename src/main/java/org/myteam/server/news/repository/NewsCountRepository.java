package org.myteam.server.news.repository;

import org.myteam.server.news.domain.NewsCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsCountRepository extends JpaRepository<NewsCount, Long> {
}
