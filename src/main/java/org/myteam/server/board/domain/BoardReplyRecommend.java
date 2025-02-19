package org.myteam.server.board.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.member.entity.Member;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_board_reply_recommend")
public class BoardReplyRecommend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private BoardReply boardReply;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    public BoardReplyRecommend(Long id, BoardReply boardReply, Member member) {
        this.id = id;
        this.boardReply = boardReply;
        this.member = member;
    }
}