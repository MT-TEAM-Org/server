package org.myteam.server.global.util.date;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class DateFormatUtil {
    public static LocalDate convertToLocalDateToDate(LocalDate localDate) {
        return Date.valueOf(localDate).toLocalDate();
    }

    public static LocalTime convertToLocalTimeToTime(LocalTime localDate) {
        return Time.valueOf(localDate).toLocalTime();
    }
}
