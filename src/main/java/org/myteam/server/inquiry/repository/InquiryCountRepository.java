package org.myteam.server.inquiry.repository;

import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.inquiry.domain.InquiryCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InquiryCountRepository extends JpaRepository<InquiryCount, Long> {
    void deleteByInquiryId(Long id);

    Optional<InquiryCount> findByInquiryId(Long inquiryId);

    @Modifying
    @Query("UPDATE InquiryCount i SET i.viewCount = :view, i.commentCount = :comment, i.recommendCount = :recommend WHERE i.inquiryId = :inquiryId")
    void updateAllCounts(@Param("inquiryId") Long inquiryId,
                         @Param("view") int view,
                         @Param("comment") int comment,
                         @Param("recommend") int recommend);
}
