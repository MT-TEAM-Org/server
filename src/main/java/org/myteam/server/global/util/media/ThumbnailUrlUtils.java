package org.myteam.server.global.util.media;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.myteam.server.global.domain.Category;

/**
 * 크롤링 된 뉴스 이미지 썸네일 url을 디코딩
 */
public class ThumbnailUrlUtils {

    public static String extractCleanThumbImg(String thumbImg, Category category) {
        if (thumbImg == null || category == null) {
            return thumbImg;
        }

        // BASEBALL, FOOTBALL만 디코딩 대상
        if (category == Category.BASEBALL || category == Category.FOOTBALL) {

            // dthumb URL 안에 src 파라미터가 있는 경우만 추출
            if (thumbImg.contains("src=")) {
                try {
                    // src=...& 이전까지만 추출
                    String encodedSrc = thumbImg.split("src=")[1].split("&")[0];
                    return URLDecoder.decode(encodedSrc, StandardCharsets.UTF_8)
                            .replace("\"", "");
                } catch (Exception e) {
                    return thumbImg; // 실패 시 원본 그대로
                }
            }

            // src 파라미터 없으면 원본 그대로
            return thumbImg;
        }

        // BASEBALL, FOOTBALL이 아닌 경우 → ? 뒤 제거
        int questionMarkIndex = thumbImg.indexOf('?');
        if (questionMarkIndex != -1) {
            return thumbImg.substring(0, questionMarkIndex);
        }

        return thumbImg;
    }
}