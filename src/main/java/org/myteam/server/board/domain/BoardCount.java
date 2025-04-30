package org.myteam.server.board.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.BaseTime;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_board_count")
public class BoardCount extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(nullable = false)
    private int recommendCount;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int commentCount;

    @Builder
    public BoardCount(Board board, int recommendCount, int commentCount, int viewCount) {
        this.board = board;
        this.recommendCount = recommendCount;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
    }

    public static BoardCount createBoardCount(Board board) {
        final int COUNT_SETTING_NUMBER = 0;

        return BoardCount.builder()
                .board(board)
                .recommendCount(COUNT_SETTING_NUMBER)
                .commentCount(COUNT_SETTING_NUMBER)
                .viewCount(COUNT_SETTING_NUMBER)
                .build();
    }

    public void addViewCount() {
        this.viewCount += 1;
    }

    public void addViewCount(int count) {
        this.viewCount += count;
    }

    public void addRecommendCount() {
        this.recommendCount += 1;
    }

    public void minusRecommendCount() {
        this.recommendCount -= 1;
    }

    public void addCommentCount() {
        this.commentCount += 1;
    }

    /**
     * 댓글 1 감소
     */
    public void minusCommentCount() {
        this.commentCount -= 1;
    }

    /**
     * 댓글 count 만큼 감소
     */
    public void minusCommentCount(int count) {
        this.commentCount -= count;
    }
}