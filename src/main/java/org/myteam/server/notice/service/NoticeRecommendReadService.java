package org.myteam.server.notice.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.notice.Repository.NoticeRecommendRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeRecommendReadService {

    private final NoticeRecommendRepository noticeRecommendRepository;

    public boolean isRecommended(Long noticeId, UUID memberPublicId) {
        return noticeRecommendRepository.findByNoticeIdAndMemberPublicId(noticeId, memberPublicId).isPresent();
    }
}
