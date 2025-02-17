package org.myteam.server.board.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "p_board_comment")
public class BoardComment extends BaseTime {

    private static final int COUNT_SETTING_NUMBER = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_id")
    private Member member;

    private String imageUrl;

    private String comment;

    private String createdIp;

    @Column(nullable = false)
    private int recommendCount;

    @Builder
    public BoardComment(Board board, Member member, String imageUrl, String comment, String createdIp,
                        int recommendCount) {
        this.board = board;
        this.member = member;
        this.imageUrl = imageUrl;
        this.comment = comment;
        this.createdIp = createdIp;
        this.recommendCount = recommendCount;
    }

    public static BoardComment createBoardComment(Board board, Member member, String imageUrl, String comment,
                                                  String createdIp) {
        final int COUNT_SETTING_NUMBER = 0;
        return BoardComment.builder()
                .board(board)
                .member(member)
                .imageUrl(imageUrl)
                .comment(comment)
                .createdIp(createdIp)
                .recommendCount(COUNT_SETTING_NUMBER)
                .build();
    }

    public void updateComment(String imageUrl, String comment) {
        this.imageUrl = imageUrl;
        this.comment = comment;
    }

    public boolean isAuthor(Member member) {
        return this.member.equals(member);
    }

    public static void verifyBoardCommentAuthor(BoardComment boardComment, Member member) {
        if (!boardComment.isAuthor(member) && !member.isAdmin()) {
            throw new PlayHiveException(ErrorCode.POST_AUTHOR_MISMATCH);
        }
    }
}
