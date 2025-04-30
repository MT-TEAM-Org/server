package org.myteam.server.board.dto.reponse;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardRankingDto {
    private Long id;
    private int viewCount;
    private int recommendCount;
    private int commentCount;
    private String title;
    private int totalScore; // 댓글수 + 조회수

    public BoardRankingDto(Long id, int viewCount, int recommendCount, int commentCount, String title, int totalScore) {
        this.id = id;
        this.viewCount = viewCount;
        this.recommendCount = recommendCount;
        this.commentCount = commentCount;
        this.title = title;
        this.totalScore = totalScore;
    }
}
