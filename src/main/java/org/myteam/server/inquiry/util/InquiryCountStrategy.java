package org.myteam.server.inquiry.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCount;
import org.myteam.server.inquiry.domain.InquiryCount;
import org.myteam.server.inquiry.repository.InquiryCountRepository;
import org.myteam.server.util.CountStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryCountStrategy implements CountStrategy {

    private final String KEY = "inquiry:count:";
    private final InquiryCountRepository inquiryCountRepository;

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
    public CommonCount<InquiryCount> loadFromDatabase(Long contentId) {
        InquiryCount inquiryCount = inquiryCountRepository.findByInquiryId(contentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.INQUIRY_NOT_FOUND));

        return new CommonCount<>(
                inquiryCount,
                inquiryCount.getViewCount(),
                inquiryCount.getCommentCount(),
                inquiryCount.getRecommendCount()
        );
    }

    @Override
    @Transactional
    public void updateToDatabase(CommonCount<?> count) {
        InquiryCount inquiryCount = (InquiryCount) count.getCount();
        Long inquiryId = inquiryCount.getInquiry().getId();

        inquiryCountRepository.updateAllCounts(
                inquiryId,
                count.getViewCount(),
                count.getCommentCount(),
                count.getRecommendCount()
        );
    }
}
