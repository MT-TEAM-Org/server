package org.myteam.server.util;

import org.myteam.server.global.util.redis.CommonCount;

public interface CountStrategy {
    String getRedisKey(Long contentId);

    String getRedisPattern();

    Long extractContentIdFromKey(String key);

    CommonCount<?> loadFromDatabase(Long contentId);     // DB에서 카운트 전체 불러오기

    void updateToDatabase(CommonCount<?> count);         // Redis → DB에 저장
}