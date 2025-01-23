package org.myteam.server.member.domain.validator;

import java.util.regex.Pattern;

public class MemberValidator {
    private static final String TEL_PATTERN = "^010[0-9]{7,8}$";
    private static final String EMAIL_PATTERN = "^[0-9a-zA-Z]+@[0-9a-zA-Z]+(\\.[a-zA-Z]{2,3}){1,2}$";

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
}
