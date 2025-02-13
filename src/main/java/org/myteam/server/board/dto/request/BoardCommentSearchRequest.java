package org.myteam.server.board.dto.request;

import lombok.Getter;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.data.repository.NoRepositoryBean;

@Getter
@NoRepositoryBean
public class BoardCommentSearchRequest {

    private BoardOrderType orderType;

    public void setOrderType(BoardOrderType orderType) {
        if (orderType != BoardOrderType.CREATE && orderType != BoardOrderType.RECOMMEND) {
            throw new PlayHiveException(ErrorCode.INVALID_TYPE);
        }
        this.orderType = orderType;
    }
}