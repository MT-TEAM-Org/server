package org.myteam.server.board.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.member.entity.Member;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_board_comment_recommend")
public class BoardCommentRecommend extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_comment_id")
    private BoardComment boardComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_id")
    private Member member;

    @Builder
    public BoardCommentRecommend(Long id, BoardComment boardComment, Member member) {
        this.id = id;
        this.boardComment = boardComment;
        this.member = member;
    }

    public static BoardCommentRecommend createBoardCommentRecommend(BoardComment boardComment, Member member) {

        return BoardCommentRecommend.builder()
                .boardComment(boardComment)
                .member(member)
                .build();
    }
}