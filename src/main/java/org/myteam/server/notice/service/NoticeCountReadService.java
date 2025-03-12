package org.myteam.server.notice.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.notice.repository.NoticeCountRepository;
import org.myteam.server.notice.domain.NoticeCount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeCountReadService {

    private final NoticeCountRepository noticeCountRepository;

    public NoticeCount findByNoticeId(Long noticeId) {
        return noticeCountRepository.findByNoticeId(noticeId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.NOTICE_COUNT_NOT_FOUND));
    }
}
