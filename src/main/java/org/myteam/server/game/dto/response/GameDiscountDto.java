package org.myteam.server.game.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GameDiscountDto {
    /**
     * 게임 할인 ID
     */
    private Long id;
    /**
     * 게임 이미지
     */
    private String thumbImg;
    /**
     * 게임명
     */
    private String title;
    /**
     * 할인 전 가격
     */
    private String originalPrice;
    /**
     * 할인율
     */
    private String discountPercent;
    /**
     * 할인된 가격
     */
    private String finalPrice;
    /**
     * 연결 링크
     */
    private String link;
    /**
     * 노출 날짜
     */
    private LocalDateTime exposureDate;

    public GameDiscountDto(Long id, String thumbImg, String title, String originalPrice, String discountPercent,
                           String finalPrice, String link, LocalDateTime exposureDate) {
        this.id = id;
        this.thumbImg = thumbImg;
        this.title = title;
        this.originalPrice = originalPrice;
        this.discountPercent = discountPercent;
        this.finalPrice = finalPrice;
        this.link = link;
        this.exposureDate = exposureDate;
    }
}