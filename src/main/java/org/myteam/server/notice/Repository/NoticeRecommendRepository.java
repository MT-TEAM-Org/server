package org.myteam.server.notice.Repository;

import org.myteam.server.notice.domain.NoticeRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoticeRecommendRepository extends JpaRepository<NoticeRecommend, Long> {

    Optional<NoticeRecommend> findByNoticeIdAndMemberPublicId(Long noticeId, UUID memberPublicId);

    void deleteByNoticeIdAndMemberPublicId(Long noticeId, UUID memberPublicId);
}
