package org.myteam.server.news.newsReply.dto.repository;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplyDto {

	@Schema(description = "뉴스 대댓글 ID")
	private Long newsReplyId;
	@Schema(description = "뉴스 댓글 ID")
	private Long newsCommentId;
	@Schema(description = "뉴스 작성자")
	private NewsReplyMemberDto member;
	@Schema(description = "뉴스 대댓글 내용")
	private String comment;
	@Schema(description = "뉴스 대댓글 작성시 IP")
	private String ip;
	@Schema(description = "뉴스 대댓글 작성날짜")
	private LocalDateTime createTime;
	@Schema(description = "뉴스 대댓글 추천 여부")
	private int recommendCount;
	@Schema(description = "뉴스 대댓글 추천 여부")
	private boolean isRecommend;

	public NewsReplyDto(Long newsReplyId, Long newsCommentId, NewsReplyMemberDto member, String comment,
		String ip, LocalDateTime createTime, int recommendCount, boolean isRecommend) {
		this.newsReplyId = newsReplyId;
		this.newsCommentId = newsCommentId;
		this.member = member;
		this.comment = comment;
		this.ip = ip;
		this.createTime = createTime;
		this.recommendCount = recommendCount;
		this.isRecommend = isRecommend;
	}

}
