package org.myteam.server.admin.utils;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DateTypeFactory {


    public List<LocalDateTime> SupplyDateTime(DateType dateType, LocalDateTime now){
        LocalDateTime now_return=now.with(LocalTime.MIDNIGHT);
        LocalDateTime now_return2=now.plusDays(1L).with(LocalTime.MIDNIGHT);
        if(dateType.name().equals(DateType.Day.name())){

            return List.of(now_return2,now_return,now_return,now_return.minusDays(1L));

        }
        if(dateType.name().equals(DateType.WeekEnd.name())){
            LocalDateTime weekend=now_return.minusWeeks(1L);

            return List.of(now_return2,weekend,weekend,weekend.minusWeeks(1L).minusDays(1L));
        }
        if(dateType.name().equals(DateType.OneMonth.name())){
            LocalDateTime month=now_return.minusMonths(1L);

            return List.of(now_return2,month,month,month.minusMonths(1L).minusDays(1L));
        }
        if(dateType.name().equals(DateType.ThreeMonth.name())){
            LocalDateTime threeMonth=now_return.minusMonths(3L);

            return List.of(now_return2,threeMonth,threeMonth,threeMonth.minusMonths(3L).minusDays(1L));
        }
        if(dateType.name().equals(DateType.SixMonth.name())){

            LocalDateTime sixMonth=now_return.minusMonths(6L);
            return List.of(now_return2,sixMonth,sixMonth,sixMonth.minusMonths(6L).minusDays(1L));
        }
        if(dateType.name().equals(DateType.Year.name())){

            LocalDateTime year=now_return.minusYears(1L);
            return List.of(now_return2,year,year,year.minusYears(1L).minusDays(1L));
        }

        throw new RuntimeException();


    }
}
