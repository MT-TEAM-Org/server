package org.myteam.server.comment.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.myteam.server.board.domain.Board;
import org.myteam.server.member.entity.Member;


@Entity
@DiscriminatorValue("BOARD")
@NoArgsConstructor
public class BoardComment extends Comment {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Builder
    public BoardComment(Member member, Member mentionedMember, String comment, String imageUrl, String createdIp,
                        Comment parent, Board board) {
        super(member, mentionedMember, comment, imageUrl, createdIp, parent, CommentType.BOARD);
        this.board = board;
    }

    public static BoardComment createComment(Board board, Member member, Member mentionedMember,
                                             String comment, String imageUrl, String createdIp, Comment parent) {
        return BoardComment.builder()
                .board(board)
                .member(member)
                .mentionedMember(mentionedMember)
                .comment(comment)
                .imageUrl(imageUrl)
                .createdIp(createdIp)
                .parent(parent)
                .build();
    }
}
