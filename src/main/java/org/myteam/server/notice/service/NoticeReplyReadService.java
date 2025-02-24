package org.myteam.server.notice.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.notice.Repository.NoticeReplyRepository;
import org.myteam.server.notice.domain.NoticeReply;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeReplyReadService {

    private final NoticeReplyRepository noticeReplyRepository;

    public NoticeReply findById(Long noticeReplyId) {
        return noticeReplyRepository.findById(noticeReplyId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.NOTICE_REPLY_NOT_FOUND));
    }

    public List<NoticeReply> findByNoticeCommentId(Long noticeCommentId) {
        return noticeReplyRepository.findByNoticeCommentId(noticeCommentId);
    }
}
