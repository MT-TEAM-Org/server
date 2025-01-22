package org.myteam.server.board.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.board.dto.BoardSaveRequest;
import org.myteam.server.member.entity.Member;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String title;

    private String content;

    private String link;

    private String createdIp;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private BoardCount boardCount;

    @Builder
    public Board(Member member, Category category, String title, String content, String link, String createdIp) {
        this.member = member;
        this.category = category;
        this.title = title;
        this.content = content;
        this.link = link;
        this.createdIp = createdIp;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateBoard(BoardSaveRequest request, Category category) {
        this.category = category;
        this.title = request.getTitle();
        this.content = request.getContent();
        this.link = request.getLink();
        this.updatedAt = LocalDateTime.now();
    }

    public BoardCount createBoardCount(Board board) {
        this.boardCount = BoardCount.builder().board(board).likeCount(0).commentCount(0).viewCount(0).build();
        return boardCount;
    }
}