package org.myteam.server.global.util.redis;

public interface RankingCacheReader {
    int getViewCount(Long boardId);
    int getTotalScore(Long boardId); // view + recommend
}
