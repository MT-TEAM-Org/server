package org.myteam.server.notice.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
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

    public void confirmExistNoticeRecommend(Long noticeId, UUID publicId) {
        noticeRecommendRepository.findByNoticeIdAndMemberPublicId(noticeId, publicId)
                .ifPresent(member -> {
                    throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND_NOTICE);
                });
    }

    public boolean isAlreadyRecommended(Long noticeId, UUID publicId) {
        if (!noticeRecommendRepository.findByNoticeIdAndMemberPublicId(noticeId, publicId).isPresent()) {
            throw new PlayHiveException(ErrorCode.NO_MEMBER_RECOMMEND_RECORD);
        }
        return true;
    }
}
