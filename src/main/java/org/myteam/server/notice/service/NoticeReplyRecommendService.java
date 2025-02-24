package org.myteam.server.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.notice.Repository.NoticeReplyRecommendRepository;
import org.myteam.server.notice.domain.NoticeReply;
import org.myteam.server.notice.domain.NoticeReplyRecommend;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeReplyRecommendService {

    private final NoticeReplyReadService noticeReplyReadService;
    private final SecurityReadService securityReadService;
    private final NoticeReplyRecommendReadService noticeReplyRecommendReadService;
    private final NoticeReplyRecommendRepository noticeReplyRecommendRepository;

    public void recommendNoticeReply(Long noticeReplyId) {
        NoticeReply noticeReply = noticeReplyReadService.findById(noticeReplyId);
        Member member = securityReadService.getMember();

        verifyMemberAlreadyRecommend(noticeReply, member);

        recommend(noticeReply, member);

        addRecommendCount(noticeReply.getId());
    }

    public void deleteRecommendNoticeReply(Long noticeReplyId) {
        NoticeReply noticeReply = noticeReplyReadService.findById(noticeReplyId);
        Member member = securityReadService.getMember();

        isAlreadyRecommended(noticeReply, member);

        noticeReplyRecommendRepository.deleteByNoticeReplyIdAndMemberPublicId(noticeReply.getId(), member.getPublicId());

        minusRecommendCount(noticeReply.getId());
    }

    /**
     * 추천이 되어있는 상태인지 검사
     */
    private void isAlreadyRecommended(NoticeReply noticeReply, Member member) {
        noticeReplyRecommendReadService.isAlreadyRecommended(noticeReply.getId(), member.getPublicId());
    }

    /**
     * 이미 추천한 상태인지 검사
     */
    private void verifyMemberAlreadyRecommend(NoticeReply noticeReply, Member member) {
        noticeReplyRecommendReadService.confirmExistNoticeReply(noticeReply.getId(), member.getPublicId());
    }

    /**
     * 공지사항 대댓글 추천 생성
     */
    private void recommend(NoticeReply noticeReply, Member member) {
        NoticeReplyRecommend noticeReplyRecommend = NoticeReplyRecommend.createNoticeReplyRecommend(noticeReply, member);
        noticeReplyRecommendRepository.save(noticeReplyRecommend);
    }

    /**
     * 공지사항 대댓글 추천수 증가
     */
    private void addRecommendCount(Long noticeReplyId) {
        NoticeReply reply = noticeReplyRecommendReadService.findById(noticeReplyId);
        reply.addRecommendCount();
    }

    /**
     * 공지사항 대댓글 추천소 감소
     */
    private void minusRecommendCount(Long noticeReplyId) {
        NoticeReply reply = noticeReplyRecommendReadService.findById(noticeReplyId);
        reply.minusRecommendCount();
    }
}
