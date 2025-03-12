package org.myteam.server.notice.repository;

import org.myteam.server.notice.domain.NoticeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeCountRepository extends JpaRepository<NoticeCount, Long> {

    Optional<NoticeCount> findByNoticeId(Long noticeId);

    void deleteByNoticeId(Long noticeId);
}
