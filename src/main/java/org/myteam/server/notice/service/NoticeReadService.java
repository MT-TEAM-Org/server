package org.myteam.server.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.notice.Repository.NoticeRepository;
import org.myteam.server.notice.domain.Notice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeReadService {

    private final NoticeRepository noticeRepository;

    public Notice findById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.NOTICE_NOT_FOUND));
    }
}
