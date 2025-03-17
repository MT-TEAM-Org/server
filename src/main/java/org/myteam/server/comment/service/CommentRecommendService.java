package org.myteam.server.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.comment.domain.CommentRecommend;
import org.myteam.server.comment.repository.CommentRecommendRepository;
import org.myteam.server.comment.repository.CommentRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentRecommendService {

    private final CommentReadService commentReadService;
    private final SecurityReadService securityReadService;
    private final CommentRecommendRepository commentRecommendRepository;
    private final CommentRecommendReadService commentRecommendReadService;

    /**
     * 댓글 추천
     */
    public void recommendComment(Long commentId) {
        Comment comment = commentReadService.findById(commentId);
        Member member = securityReadService.getMember();
        log.info("댓글 추천 요청 - commentId: {}, memberId: {}", commentId, member.getPublicId());

        // 이미 추천했는지 확인
        verifyMemberAlreadyRecommend(comment, member);

        // 추천 테이블에 삽입
        recommend(comment, member);

        // Comment 테이블 recommendCount +1
        comment.addRecommendCount();

        log.info("댓글 추천 완료 - commentId: {}, memberId: {}, recommendCount: {}",
                comment.getId(), member.getPublicId(), comment.getRecommendCount());
    }

    /**
     * 댓글 추천 취소
     */
    public void cancelRecommendComment(Long commentId) {
        Comment comment = commentReadService.findById(commentId);
        Member member = securityReadService.getMember();
        log.info("댓글 추천 취소 요청 - commentId: {}, memberId: {}", commentId, member.getPublicId());

        // 추천 여부 확인
        isAlreadyRecommended(comment, member);

        // 추천 테이블에서 삭제
        commentRecommendRepository.deleteByCommentIdAndMemberPublicId(comment.getId(), member.getPublicId());

        // recommendCount -1
        comment.minusRecommendCount();
        log.info("댓글 추천 취소 완료 - commentId: {}, memberId: {}, recommendCount: {}",
                comment.getId(), member.getPublicId(), comment.getRecommendCount());
    }

    /**
     * 추천이 되어있는 상태인지 검사
     */
    private void isAlreadyRecommended(Comment comment, Member member) {
        commentRecommendReadService.isAlreadyRecommended(comment.getId(), member.getPublicId());
    }

    /**
     * 이미 추천한 상태인지 검사
     */
    private void verifyMemberAlreadyRecommend(Comment comment, Member member) {
        commentRecommendReadService.confirmExistRecommend(comment.getId(), member.getPublicId());
    }

    /**
     * 게시판 댓글 추천 생성
     */
    private void recommend(Comment comment, Member member) {
        CommentRecommend recommend = CommentRecommend.createCommentRecommend(comment, member);
        commentRecommendRepository.save(recommend);
    }
}
