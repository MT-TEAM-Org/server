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
     * 개선 요청
     */
    SUGGESTION,
    /**
     * 전적 인증 (e-sports)
     */
    RECORD_VERIFICATION,
    /**
     * 플레이 팁 (e-sports)
     */
    PLAY_TIP;

    public void confirmEsports() {
        if (this.equals(PLAY_TIP) || this.equals(RECORD_VERIFICATION)) {
            throw new PlayHiveException(ErrorCode.INVALID_TYPE);
        }
    }
}