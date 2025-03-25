package org.myteam.server.notice.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCount;
import org.myteam.server.notice.domain.NoticeCount;
import org.myteam.server.notice.repository.NoticeCountRepository;
import org.myteam.server.util.ViewCountStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeViewCountStrategy implements ViewCountStrategy {

    private final String KEY = "view:notice:";
    private final NoticeCountRepository noticeCountRepository;

    @Override
    public String getRedisKey(Long contentId) {
        return KEY + contentId;
    }

    @Override
    public String getRedisPattern() {
        return KEY + "*";
    }

    @Override
    public Long extractContentIdFromKey(String key) {
        return Long.parseLong(key.substring(KEY.length()));
    }

    @Override
    public CommonCount loadFromDatabase(Long contentId) {
        NoticeCount noticeCount = noticeCountRepository.findByNoticeId(contentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.NOTICE_NOT_FOUND));
        return new CommonCount(noticeCount, noticeCount.getViewCount());
    }

    @Override
    @Transactional
    public void updateToDatabase(Long id, int viewCount) {
        noticeCountRepository.updateViewCount(id, viewCount);
    }
}
