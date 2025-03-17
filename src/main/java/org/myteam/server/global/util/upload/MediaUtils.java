package org.myteam.server.global.util.upload;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;

public class MediaUtils {
//    private static final String BUCKET_PREFIX = "devbucket/";

    public static String getImagePath(String url) {
        String mediaDomain = getMediaDomain();

        if (url == null || !url.contains(mediaDomain)) {
            return null; // URL이 없거나, 버킷 경로를 포함하지 않으면 null 반환
        }
        return url.substring(url.indexOf(mediaDomain) + mediaDomain.length());
    }

    public static boolean verifyImageUrlAndRequestImageUrl(String existsUrl, String requestUrl) {
        return existsUrl != null && !existsUrl.equals(requestUrl);
    }

    private static String getMediaDomain() {
        String envValue = System.getenv("AWS_URL");
        if (envValue == null) {
            throw new PlayHiveException(ErrorCode.MISSING_ENVIRONMENT_VARIABLE);
        }
        return envValue;
    }
}