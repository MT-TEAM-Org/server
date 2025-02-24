package org.myteam.server.notice.Repository;

import org.myteam.server.notice.domain.NoticeReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeReplyRepository extends JpaRepository<NoticeReply, Long> {

    List<NoticeReply> findByNoticeCommentId(Long noticeCommentId);
}
