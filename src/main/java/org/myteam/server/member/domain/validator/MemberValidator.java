package org.myteam.server.member.domain.validator;

import java.time.LocalDateTime;
import java.util.regex.Pattern;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.stereotype.Component;

@Component
public class MemberValidator {
    private static final String TEL_PATTERN = "^010[0-9]{7,8}$";
    private static final String EMAIL_PATTERN = "^[0-9a-zA-Z_]+@[0-9a-zA-Z]+(\\.[a-zA-Z]{2,3}){1,2}$";

    private static final int BIRTH_DATE_LENGTH = 6;
    private static final int MIN_MONTH = 1;
    private static final int MAX_MONTH = 12;
    private static final int MIN_DAY = 1;
    private static final int CENTURY_DIVISOR = 100;
    private static final int LEAP_YEAR_DIVISOR = 4;
    private static final int LEAP_YEAR_CENTURY_DIVISOR = 400;
    private static final int[] MONTH_DAYS = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public static String validateTel(String tel) {
        if (tel != null && Pattern.matches(TEL_PATTERN, tel)) {
            return tel; // 유효한 값 반환
        }
        return null; // 유효하지 않으면 null 반환
    }

    public static String validateEmail(String email) {
        if (email != null && Pattern.matches(EMAIL_PATTERN, email)) {
            return email; // 유효한 값 반환
        }
        return null; // 유효하지 않으면 null 반환
    }

    public void validateBirthDate(String birthDate) {
        if (!isValidLength(birthDate)) {
            throw new PlayHiveException(ErrorCode.INVALID_BIRTH_DATE);
        }

        int birthYear = Integer.parseInt(birthDate.substring(0, 2));
        int birthMonth = Integer.parseInt(birthDate.substring(2, 4));
        int birthDay = Integer.parseInt(birthDate.substring(4));

        if (!isValidMonth(birthMonth)) {
            throw new PlayHiveException(ErrorCode.INVALID_BIRTH_MONTH);
        }

        if (!isValidDay(birthYear, birthMonth, birthDay)) {
            throw new PlayHiveException(ErrorCode.INVALID_BIRTH_DAY);
        }
    }

    public boolean isValidLength(String birthDate) {
        return birthDate != null && birthDate.length() == BIRTH_DATE_LENGTH;
    }

    public boolean isValidMonth(int month) {
        return month >= MIN_MONTH && month <= MAX_MONTH;
    }

    public boolean isValidDay(int year, int month, int day) {
        int currentYear = LocalDateTime.now().getYear();
        int century = (year <= currentYear % 100) ? 2000 : 1900;
        int adjustedYear = century + year;

        int maxDay = getMaxDaysOfMonth(month, adjustedYear);
        return day >= MIN_DAY && day <= maxDay;
    }

    public int getMaxDaysOfMonth(int month, int year) {
        if (month == 2 && isLeapYear(year)) {
            return 29;
        }
        return MONTH_DAYS[month - 1];
    }

    public boolean isLeapYear(int year) {
        return (year % LEAP_YEAR_DIVISOR == 0 && year % CENTURY_DIVISOR != 0) || (year % LEAP_YEAR_CENTURY_DIVISOR
                == 0);
    }
}
