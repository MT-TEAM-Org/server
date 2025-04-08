package org.myteam.server.board.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.comment.service.CommentCountService;
import org.myteam.server.global.util.redis.RedisCountService;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.report.domain.DomainType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardCountService implements CommentCountService {

    private final BoardCountReadService boardCountReadService;
    private final RedisCountService redisCountService;

    public void recommendBoard(Long boardId) {
        redisCountService.getCommonCount(ServiceType.RECOMMEND, DomainType.BOARD, boardId, null);
    }

    public void deleteRecommendBoard(Long boardId) {
        redisCountService.getCommonCount(ServiceType.RECOMMEND_CANCEL, DomainType.BOARD, boardId, null);
    }

    /**
     * recommendCount 증가
     */
    public void addRecommendCount(Long boardId) {
        boardCountReadService.findByBoardIdLock(boardId).addRecommendCount();
    }

    /**
     * recommendCount 감소
     */
    public void minusRecommendCount(Long boardId) {
        boardCountReadService.findByBoardIdLock(boardId).minusRecommendCount();
    }

    /**
     * commentCount 증가
     */
    @Override
    public void addCommentCount(Long boardId) {
        boardCountReadService.findByBoardIdLock(boardId).addCommentCount();
    }

    /**
     * commentCount 감소 (1 감소)
     */
    @Override
    public void minusCommentCount(Long boardId) {
        boardCountReadService.findByBoardIdLock(boardId).minusCommentCount();
    }

    /**
     * commentCount 감소 (본 댓글 + 대댓글)
     */
    @Override
    public void minusCommentCount(Long boardId, int count) {
        boardCountReadService.findByBoardIdLock(boardId).minusCommentCount(count);
    }

    public void addViewCount(Long boardId) {
        boardCountReadService.findByBoardIdLock(boardId).addViewCount();
    }
}