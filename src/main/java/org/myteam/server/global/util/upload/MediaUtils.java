package org.myteam.server.global.util.upload;

public class MediaUtils {
    private static final String BUCKET_PREFIX = "devbucket/";

    public static String getImagePath(String url) {
        if (url == null || !url.contains(BUCKET_PREFIX)) {
            return null; // URL이 없거나, 버킷 경로를 포함하지 않으면 null 반환
        }
        return url.substring(url.indexOf(BUCKET_PREFIX) + BUCKET_PREFIX.length());
    }

    public static boolean verifyImageUrlAndRequestImageUrl(String existsUrl, String requestUrl) {
        return existsUrl != null && !existsUrl.equals(requestUrl);
    }
}