package org.myteam.server.util;

import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.global.util.redis.CommonCount;

public interface ViewCountStrategy {
    String getRedisKey(Long contentId);
    String getRedisPattern();
    Long extractContentIdFromKey(String key);
    CommonCount loadFromDatabase(Long contentId);

    void updateToDatabase(Long id, int viewCount);
}