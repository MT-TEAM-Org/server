package org.myteam.server.admin.utill;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class DateTypeFactory {
    public static List<LocalDateTime> SupplyDateTime(DateType dateType, LocalDateTime now) {
        LocalDateTime nowReturn = now.with(LocalTime.MIDNIGHT);
        LocalDateTime nowReturn2 = now.plusDays(1L).with(LocalTime.MIDNIGHT);
        switch (dateType.name()) {
            case "Day":
                return List.of(nowReturn2, nowReturn, nowReturn, nowReturn.minusDays(1L));
            case "WeekEnd":
                LocalDateTime weekend = nowReturn.minusWeeks(1L);
                return List.of(nowReturn2, weekend.plusDays(1L), nowReturn, weekend.minusDays(1L));
            case "OneMonth":
                LocalDateTime month = nowReturn.minusMonths(1L);
                return List.of(nowReturn2, month.plusDays(1L), nowReturn, month.minusDays(1L));
            case "ThreeMonth":
                LocalDateTime threeMonth = nowReturn.minusMonths(3L);
                return List.of(nowReturn2, threeMonth.plusDays(1L), nowReturn, threeMonth.minusDays(1L));
            case "SixMonth":
                LocalDateTime sixMonth = nowReturn.minusMonths(6L);
                return List.of(nowReturn2, sixMonth.plusDays(1L), nowReturn, sixMonth.minusDays(1L));
            case "Year":
                LocalDateTime year = nowReturn.minusYears(1L);
                return List.of(nowReturn2, year.plusDays(1L), nowReturn, year.minusDays(1L));
        }
        throw new PlayHiveException(ErrorCode.INVALID_PARAMETER);
    }
}
