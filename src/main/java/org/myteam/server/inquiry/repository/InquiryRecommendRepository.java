package org.myteam.server.inquiry.repository;

import org.myteam.server.board.domain.BoardRecommend;
import org.myteam.server.inquiry.domain.InquiryRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InquiryRecommendRepository extends JpaRepository<InquiryRecommend, Long> {

    Optional<BoardRecommend> findByBoardIdAndMemberPublicId(Long boardId, UUID memberId);

    void deleteByBoardIdAndMemberPublicId(Long boardId, UUID publicId);
}
