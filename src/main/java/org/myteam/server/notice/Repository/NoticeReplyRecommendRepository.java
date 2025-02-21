package org.myteam.server.notice.Repository;

import org.myteam.server.notice.domain.NoticeReplyRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeReplyRecommendRepository extends JpaRepository<NoticeReplyRecommend, Long> {
}
