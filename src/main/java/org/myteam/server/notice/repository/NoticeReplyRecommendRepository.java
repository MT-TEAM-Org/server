package org.myteam.server.notice.repository;

import org.myteam.server.notice.domain.NoticeReplyRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoticeReplyRecommendRepository extends JpaRepository<NoticeReplyRecommend, Long> {
    Optional<NoticeReplyRecommend> findByNoticeReplyIdAndMemberPublicId(Long noticeReplyId, UUID publicId);

    void deleteByNoticeReplyIdAndMemberPublicId(Long noticeReplyId, UUID publicId);
}
