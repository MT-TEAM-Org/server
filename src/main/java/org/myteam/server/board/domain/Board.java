package org.myteam.server.board.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.utils.AdminControlType;
import org.myteam.server.board.dto.request.BoardRequest;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.global.domain.Category;
import org.myteam.server.member.entity.Member;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_board")
public class Board extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private Category boardType;

    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    private String title;

    private String content;

    private String link;

    private String createdIp;

    private String thumbnail;

    @OneToOne(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private BoardCount boardCount;


    @Enumerated(EnumType.STRING)
    private AdminControlType adminControlType=AdminControlType.INIT;

    @Builder
    public Board(Member member, Category boardType, CategoryType categoryType, String title, String content,
                 String link, String createdIp, String thumbnail,
                 BoardCount boardCount) {
        this.member = member;
        this.boardType = boardType;
        this.categoryType = categoryType;
        this.title = title;
        this.content = content;
        this.link = link;
        this.createdIp = createdIp;
        this.thumbnail = thumbnail;
        this.boardCount = boardCount;
    }

    public void updateBoard(BoardRequest request) {
        this.boardType = request.getBoardType();
        this.categoryType = request.getCategoryType();
        this.title = request.getTitle();
        this.content = request.getContent();
        this.link = request.getLink();
        this.thumbnail = request.getThumbnail();
    }

    public void updateBoardAdminControlType(AdminControlType adminControlType){
        this.adminControlType=adminControlType;

    }


    public boolean isAuthor(Member member) {
        return this.member.equals(member);
    }
}