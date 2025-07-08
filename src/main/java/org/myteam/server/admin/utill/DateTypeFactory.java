package org.myteam.server.admin.utill;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.thymeleaf.standard.processor.StandardInlineXMLTagProcessor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class DateTypeFactory {
    public static List<LocalDateTime> SupplyDateTime(DateType dateType, LocalDateTime now){
        LocalDateTime now_return=now.with(LocalTime.MIDNIGHT);
        LocalDateTime now_return2=now.plusDays(1L).with(LocalTime.MIDNIGHT);
        if(dateType.name().equals(DateType.Day.name())){
            return List.of(now_return2,now_return,now_return,now_return.minusDays(1L));
        }
        if(dateType.name().equals(DateType.WeekEnd.name())){
            LocalDateTime weekend=now_return.minusWeeks(1L);
            return List.of(now_return2,weekend.plusDays(1L),now_return,weekend.minusDays(1L));
        }
        if(dateType.name().equals(DateType.OneMonth.name())){
            LocalDateTime month=now_return.minusMonths(1L);
            return List.of(now_return2,month.plusDays(1L),now_return,month.minusDays(1L));
        }
        if(dateType.name().equals(DateType.ThreeMonth.name())){
            LocalDateTime threeMonth=now_return.minusMonths(3L);
            return List.of(now_return2,threeMonth.plusDays(1L),now_return,threeMonth.minusDays(1L));
        }
        if(dateType.name().equals(DateType.SixMonth.name())){
            LocalDateTime sixMonth=now_return.minusMonths(6L);
            return List.of(now_return2,sixMonth.plusDays(1L),now_return,sixMonth.minusDays(1L));
        }
        if(dateType.name().equals(DateType.Year.name())){
            LocalDateTime year=now_return.minusYears(1L);
            return List.of(now_return2,year.plusDays(1L),now_return,year.minusDays(1L));
        }
        throw new PlayHiveException(ErrorCode.INVALID_PARAMETER);
    }



}
