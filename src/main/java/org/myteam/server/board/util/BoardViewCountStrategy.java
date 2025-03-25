package org.myteam.server.board.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.board.repository.BoardCountRepository;
import org.myteam.server.board.service.BoardCountReadService;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCount;
import org.myteam.server.util.ViewCountStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardViewCountStrategy implements ViewCountStrategy {

    private final String KEY = "view:board:";
    private final BoardCountRepository boardCountRepository;

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
        BoardCount boardCount = boardCountRepository.findByBoardId(contentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_NOT_FOUND));
        return new CommonCount(boardCount, boardCount.getViewCount());
    }

    @Override
    @Transactional
    public void updateToDatabase(Long id, int viewCount) {
        boardCountRepository.updateViewCount(id, viewCount);
    }
}
