package org.myteam.server.util;

import org.myteam.server.board.domain.BoardCount;

public interface ViewCountStrategy {
    String getRedisKey(Long contentId);
    String getRedisPattern();
    Long extractContentIdFromKey(String key);
    BoardCount loadFromDatabase(Long contentId);

    void updateToDatabase(Long id, int viewCount);
}