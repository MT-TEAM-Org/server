package org.myteam.server.inquiry.repository;

import org.myteam.server.board.domain.BoardComment;
import org.myteam.server.inquiry.domain.InquiryComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryCommentRepository extends JpaRepository<InquiryComment, Long> {
}