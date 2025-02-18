package org.myteam.server.inquiry.domain;

public enum InquirySearchType {
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
