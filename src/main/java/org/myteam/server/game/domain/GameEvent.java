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
@Entity(name = "p_game_event")
public class GameEvent extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 이벤트 이미지
     */
    private String thumbImg;
    /**
     * 이벤트 제목
     */
    private String title;
    /**
     * 이벤트 설명
     */
    private String description;
    /**
     * 이벤트 기간
     */
    private String period;
    /**
     * 연결 링크
     */
    private String link;
    /**
     * 노출 날짜
     */
    private LocalDateTime exposureDate;

    @Builder
    public GameEvent(String thumbImg, String title, String description, String period, String link,
                     LocalDateTime exposureDate) {
        this.thumbImg = thumbImg;
        this.title = title;
        this.description = description;
        this.period = period;
        this.link = link;
        this.exposureDate = exposureDate;
    }
}