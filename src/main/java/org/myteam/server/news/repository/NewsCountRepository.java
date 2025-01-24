package org.myteam.server.news.repository;

import org.myteam.server.news.entity.NewsCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsCountRepository extends JpaRepository<NewsCount, Long> {
}
