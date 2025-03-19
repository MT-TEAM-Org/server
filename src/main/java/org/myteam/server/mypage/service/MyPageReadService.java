package org.myteam.server.mypage.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.dto.reponse.BoardListResponse;
//import org.myteam.server.board.service.BoardCommentReadService;
import org.myteam.server.board.service.BoardReadService;
//import org.myteam.server.board.service.BoardReplyReadService;
import org.myteam.server.comment.service.CommentReadService;
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
    private final InquiryReadService inquiryReadService;
    private final CommentReadService commentReadService;

    /**
     *
     */
    public MemberStatsResponse getMemberInfo() {
        Member member = securityReadService.getMember();
        UUID publicId = member.getPublicId();
        log.info("내 정보: {} 조회 요청", publicId);

        int postCount = boardReadService.getMyBoardListCount(publicId);
        long commentCount = commentReadService.getMyCommentCount();
        int inquiryCount = inquiryReadService.getInquiriesCountByMember(publicId);

        log.info("내 정보: {} 조회 성공", publicId);

        return MemberStatsResponse.createResponse(member, postCount, commentCount, inquiryCount);
    }

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
}
