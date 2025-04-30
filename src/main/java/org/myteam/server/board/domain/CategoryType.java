package org.myteam.server.board.domain;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;

public enum CategoryType {
    /**
     * 자유
     */
    FREE,
    /**
     * 질문
     */
    QUESTION,
    /**
     * 이슈
     */
    ISSUE,
    /**
     * 전적 인증 (e-sports)
     */
    VERIFICATION,
    /**
     * 플레이 팁 (e-sports)
     */
    TIP,
    UNKNOWN;

    public void confirmEsports() {
        if (this.equals(TIP) || this.equals(VERIFICATION)) {
            throw new PlayHiveException(ErrorCode.INVALID_TYPE);
        }
    }
}