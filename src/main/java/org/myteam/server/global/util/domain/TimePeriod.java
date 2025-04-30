package org.myteam.server.global.util.domain;

import java.time.LocalDateTime;

public enum TimePeriod {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    ALL;

    public LocalDateTime getStartDateByTimePeriod(TimePeriod timePeriod) {
        LocalDateTime now = LocalDateTime.now();

        return switch (timePeriod) {
            case DAILY -> now.minusDays(1);
            case WEEKLY -> now.minusWeeks(1);
            case MONTHLY -> now.minusMonths(1);
            case YEARLY -> now.minusYears(1);
            case ALL -> null; // 전체 기간은 필터 없음
        };
    }
}
