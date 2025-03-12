package org.myteam.server.notice.repository;

import org.myteam.server.notice.domain.NoticeCommentRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoticeCommentRecommendRepository extends JpaRepository<NoticeCommentRecommend, Long> {

    Optional<NoticeCommentRecommend> findByNoticeCommentIdAndMemberPublicId(Long noticeCommentId, UUID memberPublicId);

    void deleteByNoticeCommentIdAndMemberPublicId(Long noticeCommentId, UUID memberPublicId);
}
