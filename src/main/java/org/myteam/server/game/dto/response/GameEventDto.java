package org.myteam.server.game.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GameEventDto {
    /**
     * 게임 이벤트 ID
     */
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

    public GameEventDto(Long id, String thumbImg, String title, String description, String period, String link,
                        LocalDateTime exposureDate) {
        this.id = id;
        this.thumbImg = thumbImg;
        this.title = title;
        this.description = description;
        this.period = period;
        this.link = link;
        this.exposureDate = exposureDate;
    }
}