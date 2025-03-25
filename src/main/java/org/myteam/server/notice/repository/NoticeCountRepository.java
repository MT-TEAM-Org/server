package org.myteam.server.notice.repository;

import java.util.Optional;
import org.myteam.server.notice.domain.NoticeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeCountRepository extends JpaRepository<NoticeCount, Long> {

    Optional<NoticeCount> findByNoticeId(Long noticeId);

    void deleteByNoticeId(Long noticeId);

    @Modifying
    @Query("UPDATE NoticeCount n SET n.viewCount = :viewCount WHERE n.id = :noticeId")
    void updateViewCount(@Param("noticeId") Long noticeId, @Param("viewCount") int viewCount);
}
