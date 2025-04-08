package org.myteam.server.board.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.board.repository.BoardCountRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCount;
import org.myteam.server.util.CountStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardCountStrategy implements CountStrategy {

    private final String KEY = "board:count:";
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
    public CommonCount<BoardCount> loadFromDatabase(Long contentId) {
        BoardCount boardCount = boardCountRepository.findByBoardId(contentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_NOT_FOUND));

        return new CommonCount<>(
                boardCount,
                boardCount.getViewCount(),
                boardCount.getCommentCount()
        );
    }

    @Override
    @Transactional
    public void updateToDatabase(CommonCount<?> count) {
        BoardCount boardCount = (BoardCount) count.getCount();
        Long boardId = boardCount.getBoard().getId();

        boardCountRepository.updateAllCounts(
                boardId,
                count.getViewCount(),
                count.getCommentCount(),
                count.getRecommendCount()
        );
    }
}
