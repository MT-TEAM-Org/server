package org.myteam.server.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.notice.Repository.NoticeRecommendRepository;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.domain.NoticeRecommend;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeCountService {

    private final SecurityReadService securityReadService;
    private final NoticeReadService noticeReadService;
    private final NoticeCountReadService noticeCountReadService;
    private final NoticeRecommendReadService noticeRecommendReadService;
    private final NoticeRecommendRepository noticeRecommendRepository;

    public void recommendNotice(Long noticeId) {
        Notice notice = noticeReadService.findById(noticeId);
        Member member = securityReadService.getMember();

        verifyMemberAlreadyRecommend(notice, member);

        recommend(notice, member);

        addRecommendCount(notice.getId());
    }

    /**
     * 추천이 되어있는 상태인지 검사
     */
    private void isAlreadyRecommended(Notice notice, Member member) {
        noticeRecommendReadService.isAlreadyRecommended(notice.getId(), member.getPublicId());
    }

    /**
     * 이미 추천한 상태인지 검사
     */
    private void verifyMemberAlreadyRecommend(Notice notice, Member member) {
        noticeRecommendReadService.confirmExistBoardRecommend(notice.getId(), member.getPublicId());
    }

    /**
     * 게시글 추천 생성
     */
    private void recommend(Notice notice, Member member) {
        NoticeRecommend recommend = NoticeRecommend.builder()
                .notice(notice)
                .member(member)
                .build();
        noticeRecommendRepository.save(recommend);
    }

    /**
     * recommendCount 증가
     */
    public void addRecommendCount(Long noticeId) {
        noticeCountReadService.findByNoticeId(noticeId).addRecommendCount();
    }

    /**
     * recommendCount 감소
     */
    public void minusRecommendCount(Long noticeId) {
        noticeCountReadService.findByNoticeId(noticeId).minusRecommendCount();
    }

    /**
     * commentCount 증가
     */
    public void addCommentCount(Long noticeId) {
        noticeCountReadService.findByNoticeId(noticeId).addCommentCount();
    }

    /**
     * commentCount 감소 (1 감소)
     */
    public void minusCommentCount(Long noticeId) {
        noticeCountReadService.findByNoticeId(noticeId).minusCommentCount();
    }

    /**
     * commentCount 감소 (본 댓글 + 대댓글)
     */
    public void minusCommentCount(Long noticeId, int count) {
        noticeCountReadService.findByNoticeId(noticeId).minusCommentCount(count);
    }

    public void addViewCount(Long noticeId) {
        noticeCountReadService.findByNoticeId(noticeId).addViewCount();
    }
}
