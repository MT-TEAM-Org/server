package org.myteam.server.news.news.dto.repository;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Setter;
import org.myteam.server.board.dto.reponse.CommentSearchDto;
import org.myteam.server.global.domain.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsDto {

	@Schema(description = "뉴스 ID")
	private Long id;
	@Schema(description = "뉴스 카테고리")
	private Category category;
	@Schema(description = "뉴스 제목")
	private String title;
	@Schema(description = "뉴스 썸네일 이미지")
	private String thumbImg;
	@Schema(description = "뉴스 본문")
	private String content;
	@Schema(description = "뉴스 댓글 수")
	private int commentCount;
	@Schema(description = "뉴스 계시 날짜")
	private LocalDateTime postDate;
	@Schema(description = "댓글 검색 시 최상단 댓글 데이터")
	private NewsCommentSearchDto newsCommentSearchDto;
	@Schema(description = "Hot 계시물 여부")
	private boolean isHot;

	@Setter
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private CommentSearchDto commentSearchList;

	public NewsDto(Long id, Category category, String title, String thumbImg, String content, int commentCount, LocalDateTime postDate, boolean isHot) {
		this.id = id;
		this.category = category;
		this.title = title;
		this.thumbImg = thumbImg;
		this.content = content;
		this.commentCount = commentCount;
		this.postDate = postDate;
		this.isHot = isHot;
	}

	public void updateNewsCommentSearchDto(NewsCommentSearchDto newsCommentSearchDto){
		this.newsCommentSearchDto = newsCommentSearchDto;
	}
}
