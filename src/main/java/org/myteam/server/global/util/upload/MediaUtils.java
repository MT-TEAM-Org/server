package org.myteam.server.global.util.upload;

public class MediaUtils {
    public static String getImagePath(String url) {
        String target = "devbucket/";
        int index = url.indexOf(target);
        if (index != -1) {
            return url.substring(index + target.length());
        }
        return null;
    }

    public static boolean verifyImageUrlAndRequestImageUrl(String existsUrl, String requestUrl) {
        return existsUrl.equals(requestUrl);
    }
}