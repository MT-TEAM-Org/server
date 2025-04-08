package org.myteam.server.notice.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCount;
import org.myteam.server.notice.domain.NoticeCount;
import org.myteam.server.notice.repository.NoticeCountRepository;
import org.myteam.server.util.CountStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeCountStrategy implements CountStrategy {

    private final String KEY = "notice:count:";
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
    public CommonCount<NoticeCount> loadFromDatabase(Long contentId) {
        NoticeCount noticeCount = noticeCountRepository.findByNoticeId(contentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.NOTICE_NOT_FOUND));

        return new CommonCount<>(
                noticeCount,
                noticeCount.getViewCount(),
                noticeCount.getCommentCount(),
                noticeCount.getRecommendCount()
        );
    }

    @Override
    @Transactional
    public void updateToDatabase(CommonCount<?> count) {
        NoticeCount noticeCount = (NoticeCount) count.getCount();
        Long noticeId = noticeCount.getNotice().getId();

        noticeCountRepository.updateAllCounts(
                noticeId,
                count.getViewCount(),
                count.getCommentCount(),
                count.getRecommendCount()
        );
    }
}
