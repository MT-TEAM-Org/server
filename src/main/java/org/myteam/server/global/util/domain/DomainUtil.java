package org.myteam.server.global.util.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DomainUtil {
    public static String extractDomain(String str) {
        String regPattern = "([a-z\\d\\-]+(?:\\.(?:asia|gg|info|name|mobi|shop|com|net|org|biz|tel|xxx|kr|co|so|me|eu|cc|or|pe|ne|re|tv|jp|tw|edu\\.vn|vn)){1,2})(?::\\d{1,5})?(?:/[^\\?]*)?(?:\\?.+)?$";
        Pattern pattern = Pattern.compile(regPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            return matcher.group(1); // 첫 번째 그룹 매칭 반환
        }
        throw new IllegalArgumentException("유효하지 않은 URL 또는 도메인: " + str);
    }
}
