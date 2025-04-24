package org.myteam.server.recommend;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.domain.NoticeRecommend;
import org.myteam.server.notice.repository.NoticeRecommendRepository;
import org.myteam.server.notice.repository.NoticeRepository;
import org.myteam.server.notice.service.NoticeRecommendReadService;
import org.myteam.server.report.domain.DomainType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeRecommendHandler implements RecommendHandler {
    private final NoticeRecommendReadService noticeRecommendReadService;
    private final NoticeRecommendRepository noticeRecommendRepository;
    private final NoticeRepository noticeRepository;

    @Override
    public boolean supports(DomainType type) {
        return type.name().equalsIgnoreCase("notice");
    }

    @Override
    public boolean isAlreadyRecommended(Long contentId, UUID userId) {
        // Redis Set 조회나 DB 조회
        return noticeRecommendReadService.isRecommended(contentId, userId);
    }

    @Override
    public void saveRecommendation(Long contentId, Member member) {
        // 저장 또는 큐에 넣기
        Notice notice = noticeRepository.findById(contentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_NOT_FOUND));
        NoticeRecommend recommend = NoticeRecommend.builder().notice(notice).member(member).build();
        noticeRecommendRepository.save(recommend);
    }

    @Override
    public void deleteRecommendation(Long contentId, UUID userId) {
        noticeRecommendRepository.deleteByNoticeIdAndMemberPublicId(contentId, userId);
    }
}
