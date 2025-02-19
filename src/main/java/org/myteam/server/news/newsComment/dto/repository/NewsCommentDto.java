package org.myteam.server.news.newsComment.dto.repository;

import java.time.LocalDateTime;

import org.myteam.server.news.RecommendYN;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentDto {

	@Schema(description = "뉴스 댓글 ID")
	private Long newsCommentId;
	@Schema(description = "뉴스 ID")
	private Long newsId;
	@Schema(description = "뉴스 작성자")
	private NewsCommentMemberDto memberDto;
	@Schema(description = "뉴스 댓글")
	private String comment;
	@Schema(description = "뉴스 댓글 작성시 IP")
	private String ip;
	@Schema(description = "뉴스 댓글 날짜")
	private LocalDateTime createTime;
	@Schema(description = "뉴스 댓글 추천 여부")
	private RecommendYN recommendYN;

	public NewsCommentDto(Long newsCommentId, Long newsId, NewsCommentMemberDto memberDto, String comment, String ip, LocalDateTime createTime, boolean recommend) {
		this.newsCommentId = newsCommentId;
		this.newsId = newsId;
		this.memberDto = memberDto;
		this.comment = comment;
		this.ip = ip;
		this.createTime = createTime;
		this.recommendYN = RecommendYN.createRecommendYN(recommend);
	}
}
