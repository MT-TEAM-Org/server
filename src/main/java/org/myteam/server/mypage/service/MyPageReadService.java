package org.myteam.server.mypage.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.dto.reponse.BoardListResponse;
//import org.myteam.server.board.service.BoardCommentReadService;
import org.myteam.server.board.service.BoardReadService;
//import org.myteam.server.board.service.BoardReplyReadService;
import org.myteam.server.improvement.domain.Improvement;
//import org.myteam.server.improvement.domain.ImprovementReply;
//import org.myteam.server.improvement.service.ImprovementCommentReadService;
//import org.myteam.server.improvement.service.ImprovementReplyReadService;
//import org.myteam.server.inquiry.service.InquiryCommentReadService;
import org.myteam.server.inquiry.service.InquiryReadService;
//import org.myteam.server.inquiry.service.InquiryReplyReadService;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.mypage.dto.request.MyBoardServiceRequest;
import org.myteam.server.mypage.dto.response.MyPageResponse.MemberModifyResponse;
import org.myteam.server.mypage.dto.response.MyPageResponse.MemberStatsResponse;
//import org.myteam.server.news.newsComment.service.NewsCommentReadService;
//import org.myteam.server.news.newsReply.service.NewsReplyReadService;
//import org.myteam.server.notice.service.NoticeCommentReadService;
//import org.myteam.server.notice.service.NoticeReplyReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageReadService {

    private final SecurityReadService securityReadService;
    private final BoardReadService boardReadService;

//    private final BoardCommentReadService boardCommentReadService;
//    private final BoardReplyReadService boardReplyReadService;
//    private final ImprovementCommentReadService improvementCommentReadService;
//    private final ImprovementReplyReadService improvementReplyReadService;
//    private final InquiryCommentReadService inquiryCommentReadService;
//    private final InquiryReplyReadService inquiryReplyReadService;
//    private final NewsCommentReadService newsCommentReadService;
//    private final NewsReplyReadService newsReplyReadService;
//    private final NoticeCommentReadService noticeCommentReadService;
//    private final NoticeReplyReadService noticeReplyReadService;

    private final InquiryReadService inquiryReadService;

    /**
     * TODO: 보드 전체 조회
     * TODO: 댓글은 어떻게 구현할지 생각해봐야할듯
     *
     * @return
     */
//    public MemberStatsResponse getMemberInfo() {
//        Member member = securityReadService.getMember();
//        UUID publicId = member.getPublicId();
//        log.info("내 정보: {} 조회 요청", publicId);
//
//        int postCount = boardReadService.getMyBoardListCount(publicId);
//        int commentCount = getTotalCommentCount(publicId);
//        int inquiryCount = inquiryReadService.getInquiriesCountByMember(publicId);
//
//        log.info("내 정보: {} 조회 성공", publicId);
//
//        return MemberStatsResponse.createResponse(member, postCount, commentCount, inquiryCount);
//    }

    public MemberModifyResponse getMemberAllInfo() {
        Member member = securityReadService.getMember();
        log.info("내 수정 정보: {} 조회 요청", member.getPublicId());

        log.info("내 수정 정보: {} 조회 성공", member.getPublicId());
        return MemberModifyResponse.createResponse(member);
    }

    public BoardListResponse getMemberPosts(MyBoardServiceRequest myBoardServiceRequest) {
        Member member = securityReadService.getMember();
        log.info("내가 쓴 게시글: {} 조회 요청", member.getPublicId());
        return boardReadService.getMyBoardList(myBoardServiceRequest, member.getPublicId());
    }

    /**
     * 내가 작성한 모든 댓글 수를 계산하는 메서드
     */
//    private int getTotalCommentCount(UUID publicId) {
//        return boardCommentReadService.getCommentCountByMemberPublicId(publicId)
//                + boardReplyReadService.getReplyCountByMemberPublicId(publicId)
//                + improvementCommentReadService.getCommentCountByMemberPublicId(publicId)
//                + improvementReplyReadService.getReplyCountByMemberPublicId(publicId)
//                + inquiryCommentReadService.getCommentCountByMemberPublicId(publicId)
//                + inquiryReplyReadService.getReplyCountByMemberPublicId(publicId)
//                + newsCommentReadService.getCommentCountByMemberPublicId(publicId)
//                + newsReplyReadService.getReplyCountByMemberPublicId(publicId);
//                + noticeCommentReadService.getCommentCountByMemberPublicId(publicId)
//                + noticeReplyReadService.getReplyCountByMemberPublicId(publicId);
//    }
}
