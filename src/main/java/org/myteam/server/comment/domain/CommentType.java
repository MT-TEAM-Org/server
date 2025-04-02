package org.myteam.server.comment.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentType {
    BOARD(BoardComment.class),
    IMPROVEMENT(ImprovementComment.class),
    INQUIRY(InquiryComment.class),
    NEWS(NewsComment.class),
    NOTICE(NoticeComment.class),
    MATCH(MatchComment.class);

    private final Class<? extends Comment> entityClass;

    public Class<? extends Comment> getEntityClass() {
        return entityClass;
    }

    // JSON에서 String을 Enum으로 변환하는 로직 추가
    @JsonCreator
    public static CommentType fromString(String value) {
        for (CommentType type : CommentType.values()) {
            if (type.name().equalsIgnoreCase(value)) { // 대소문자 무시 변환
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid CommentType: " + value);
    }
}