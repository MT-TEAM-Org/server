package org.myteam.server.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.repository.MemberRepository;
import org.myteam.server.notice.Repository.NoticeCommentRepository;
import org.myteam.server.notice.domain.NoticeComment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeCommentReadService {

    private final NoticeCommentRepository noticeCommentRepository;
    private final MemberRepository memberRepository;

    public NoticeComment findById(Long noticeCommentId) {
        return noticeCommentRepository.findById(noticeCommentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.NOTICE_COMMENT_NOT_FOUND));
    }
}
