package org.myteam.server.improvement.domain;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;

public enum ImprovementStatus {
    PENDING("접수 전"),
    RECEIVED("접수 완료"),
    COMPLETED("개선 완료");

    private final String description;

    ImprovementStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public ImprovementStatus nextStatus() {
        switch (this) {
            case PENDING:
                return RECEIVED;
            case RECEIVED:
                return COMPLETED;
            default:
                throw new PlayHiveException(ErrorCode.INVALID_IMPROVEMENT_STATUS);
        }
    }
}