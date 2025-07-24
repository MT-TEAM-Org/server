package org.myteam.server.global.util.date;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class DateFormatUtil {
    public final static DateTimeFormatter formatByDotAndSlash=
            DateTimeFormatter.ofPattern("yyyy.MM.dd/hh:mm:ss");
    public final static DateTimeFormatter formatByDot=
            DateTimeFormatter.ofPattern("yyyy.MM.dd");
    public static final DateTimeFormatter FLEXIBLE_NANO_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss") // 기본 날짜-시간 패턴
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true) // 소수점 초 (나노초)를 0~9자리까지 선택적으로 추가
            .toFormatter();
    public static LocalDate convertToLocalDateToDate(LocalDate localDate) {
        return Date.valueOf(localDate).toLocalDate();
    }
    public static LocalTime convertToLocalTimeToTime(LocalTime localDate) {
        return Time.valueOf(localDate).toLocalTime();
    }
}
