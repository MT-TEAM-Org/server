package org.myteam.server.news.newsReply.dto.service.response;

import java.time.LocalDateTime;

import org.myteam.server.member.entity.Member;
import org.myteam.server.news.newsReply.domain.NewsReply;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplyResponse {

	@Schema(description = "뉴스 대댓글 ID")
	private Long newsReplyId;
	@Schema(description = "뉴스 댓글 ID")
	private Long newsCommentId;
	@Schema(description = "뉴스 작성자")
	private NewsReplyMemberResponse member;
	@Schema(description = "뉴스 대댓글 내용")
	private String comment;
	@Schema(description = "뉴스 대댓글 작성날짜")
	private LocalDateTime createDate;

	@Builder
	public NewsReplyResponse(Long newsReplyId, Long newsCommentId, NewsReplyMemberResponse member, String comment,
		LocalDateTime createDate) {
		this.newsReplyId = newsReplyId;
		this.newsCommentId = newsCommentId;
		this.member = member;
		this.comment = comment;
		this.createDate = createDate;
	}

	public static NewsReplyResponse createResponse(NewsReply newsReply, Member member) {
		return NewsReplyResponse.builder()
			.newsReplyId(newsReply.getId())
			.newsCommentId(newsReply.getNewsComment().getId())
			.member(
				NewsReplyMemberResponse.builder()
					.publicId(member.getPublicId())
					.nickName(member.getNickname())
					.build()
			)
			.comment(newsReply.getComment())
			.createDate(newsReply.getCreateDate())
			.build();
	}
}
