package org.myteam.server.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd (E) HH:mm", Locale.KOREAN);

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }
}
