package org.myteam.server.global.util.file;

import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class MediaUtils {

    private static Map<String, MediaType> mediaMap;
    // 사용가능한 확장자

    static {
        mediaMap = new HashMap<String, MediaType>();
        mediaMap.put("JPG", MediaType.IMAGE_JPEG);
        mediaMap.put("GIF", MediaType.IMAGE_GIF);
        mediaMap.put("PNG", MediaType.IMAGE_PNG);
    }

    public static MediaType getMediaType(String type) {
        return mediaMap.get(type.toUpperCase());
    }
}
