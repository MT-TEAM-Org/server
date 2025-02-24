package org.myteam.server.notice.domain;

public enum NoticeSearchType {
    /**
     * 제목
     */
    TITLE,
    /**
     * 내용
     */
    CONTENT,
    /**
     * 제목 + 내용
     */
    TITLE_CONTENT,
    /**
     * 작성자
     */
    NICKNAME,
    /**
     * 댓글
     */
    COMMENT
}
