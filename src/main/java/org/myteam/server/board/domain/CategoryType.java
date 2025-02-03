package org.myteam.server.board.domain;

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
    PLAY_TIP
}