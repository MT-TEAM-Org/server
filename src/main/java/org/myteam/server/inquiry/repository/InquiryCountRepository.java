package org.myteam.server.inquiry.repository;

import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.inquiry.domain.InquiryCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InquiryCountRepository extends JpaRepository<InquiryCount, Long> {
    void deleteByInquiryId(Long id);

    Optional<InquiryCount> findByInquiryId(Long inquiryId);
}
