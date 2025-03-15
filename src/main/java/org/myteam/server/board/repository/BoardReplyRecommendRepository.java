//package org.myteam.server.board.repository;
//
//import java.util.Optional;
//import java.util.UUID;
//import org.myteam.server.board.domain.BoardReplyRecommend;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface BoardReplyRecommendRepository extends JpaRepository<BoardReplyRecommend, Long> {
//
//    Optional<BoardReplyRecommend> findByBoardReplyIdAndMemberPublicId(Long boardReplyId, UUID publicId);
//
//    void deleteByBoardReplyIdAndMemberPublicId(Long boardReplyId, UUID publicId);
//
//    void deleteAllByBoardReplyId(Long boardReplyId);
//}
