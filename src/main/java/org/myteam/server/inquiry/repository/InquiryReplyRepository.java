package org.myteam.server.inquiry.repository;

import org.myteam.server.inquiry.domain.InquiryReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryReplyRepository extends JpaRepository<InquiryReply, Long> {

    List<InquiryReply> findByInquiryCommentId(Long inquiryCommentId);
}
