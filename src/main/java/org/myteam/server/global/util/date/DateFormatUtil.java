package org.myteam.server.global.util.date;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateFormatUtil {
    public final static DateTimeFormatter formatByDotAndSlash=
            DateTimeFormatter.ofPattern("yyyy.MM.dd/hh:mm:ss");
    public final static DateTimeFormatter formatByDot=
            DateTimeFormatter.ofPattern("yyyy.MM.dd");
    public static LocalDate convertToLocalDateToDate(LocalDate localDate) {
        return Date.valueOf(localDate).toLocalDate();
    }
    public static LocalTime convertToLocalTimeToTime(LocalTime localDate) {
        return Time.valueOf(localDate).toLocalTime();
    }
}
