package org.myteam.server.game.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.BaseTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "p_game_discount")
public class GameDiscount extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Builder
    public GameDiscount(String thumbImg, String title, String originalPrice, String discountPercent,
                        String finalPrice,
                        String link, LocalDateTime exposureDate) {
        this.thumbImg = thumbImg;
        this.title = title;
        this.originalPrice = originalPrice;
        this.discountPercent = discountPercent;
        this.finalPrice = finalPrice;
        this.link = link;
        this.exposureDate = exposureDate;
    }
}