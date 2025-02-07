package org.myteam.server.news.newsReply.dto.service.response;

import java.time.LocalDateTime;

import org.myteam.server.member.entity.Member;
import org.myteam.server.news.newsReply.domain.NewsReply;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplyResponse {

	private Long newsReplyId;
	private Long newsCommentId;
	private NewsReplyMemberResponse member;
	private String comment;
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
					.memberId(member.getId())
					.nickName(member.getNickname())
					.build()
			)
			.comment(newsReply.getComment())
			.createDate(newsReply.getCreateDate())
			.build();
	}
}
