//package org.myteam.server.notice.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.myteam.server.member.entity.Member;
//import org.myteam.server.member.service.SecurityReadService;
//import org.myteam.server.notice.repository.NoticeCommentRecommendRepository;
//import org.myteam.server.notice.domain.NoticeComment;
//import org.myteam.server.notice.domain.NoticeCommentRecommend;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class NoticeCommentRecommendService {
//
//    private final NoticeCommentReadService noticeCommentReadService;
//    private final SecurityReadService securityReadService;
//    private final NoticeCommentRecommendReadService noticeCommentRecommendReadService;
//    private final NoticeCommentRecommendRepository noticeCommentRecommendRepository;
//
//    public void recommendNoticeComment(Long noticeCommentId) {
//        NoticeComment noticeComment = noticeCommentReadService.findById(noticeCommentId);
//        Member member = securityReadService.getMember();
//
//        verifyMemberAlreadyRecommend(noticeComment, member);
//
//        recommend(noticeComment, member);
//
//        addRecommendCount(noticeComment.getId());
//    }
//
//    public void deleteRecommendNoticeComment(Long noticeCommentId) {
//        NoticeComment noticeComment = noticeCommentReadService.findById(noticeCommentId);
//        Member member = securityReadService.getMember();
//
//        isAlreadyRecommended(noticeComment, member);
//
//        noticeCommentRecommendRepository.deleteByNoticeCommentIdAndMemberPublicId(
//                noticeComment.getId(), member.getPublicId());
//
//        minusRecommendCount(noticeCommentId);
//    }
//
//    /**
//     * 추천이 되어있는 상태인지 검사
//     */
//    private void isAlreadyRecommended(NoticeComment noticeComment, Member member) {
//        noticeCommentRecommendReadService.isAlreadyRecommended(noticeComment.getId(), member.getPublicId());
//    }
//
//    /**
//     * 이미 추천한 상태인지 검사
//     */
//    private void verifyMemberAlreadyRecommend(NoticeComment noticeComment, Member member) {
//        noticeCommentRecommendReadService.confirmExistNoticeCommentRecommend(noticeComment.getId(), member.getPublicId());
//    }
//
//    /**
//     * 공지사항 댓글 추천 생성
//     */
//    private void recommend(NoticeComment noticeComment, Member member) {
//        NoticeCommentRecommend recommend = NoticeCommentRecommend.createNoticeCommentRecommend(noticeComment, member);
//        noticeCommentRecommendRepository.save(recommend);
//    }
//
//    /**
//     * 공지사항 댓글 추천수 증가
//     */
//    private void addRecommendCount(Long noticeCommentId) {
//        NoticeComment noticeComment = noticeCommentRecommendReadService.findById(noticeCommentId);
//        noticeComment.addRecommendCount();
//    }
//
//    /**
//     * 공지사항 댓글 추천소 감소
//     */
//    private void minusRecommendCount(Long noticeCommentId) {
//        NoticeComment noticeComment = noticeCommentRecommendReadService.findById(noticeCommentId);
//        noticeComment.minusRecommendCount();
//    }
//}
