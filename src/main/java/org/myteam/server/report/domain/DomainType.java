package org.myteam.server.report.domain;

import org.myteam.server.comment.domain.CommentType;

public enum DomainType {
    NEWS, BOARD, INQUIRY, COMMENT, IMPROVEMENT, NOTICE;

    public static DomainType changeType(CommentType type) {
        return switch (type) {
            case BOARD -> DomainType.BOARD;
            case NEWS -> DomainType.NEWS;
            case NOTICE -> DomainType.NOTICE;
            case IMPROVEMENT -> DomainType.IMPROVEMENT;
            case INQUIRY -> DomainType.INQUIRY;
            default -> null;
        };
    }
}