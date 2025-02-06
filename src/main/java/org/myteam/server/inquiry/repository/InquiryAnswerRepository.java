package org.myteam.server.inquiry.repository;

import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {
    InquiryAnswer findByInquiryId(Long inquiryId);
}
