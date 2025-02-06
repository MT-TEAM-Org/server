package org.myteam.server.news.dto.service.response;

import java.time.LocalDateTime;

import org.myteam.server.member.entity.Member;
import org.myteam.server.news.domain.NewsComment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentResponse {

	private Long newsCommentId;
	private Long newsId;
	private NewsCommentMemberResponse member;
	private String comment;
	private LocalDateTime createDate;

	@Builder
	public NewsCommentResponse(Long newsCommentId, Long newsId, NewsCommentMemberResponse member, String comment, LocalDateTime createDate) {
		this.newsCommentId = newsCommentId;
		this.newsId = newsId;
		this.member = member;
		this.comment = comment;
		this.createDate = createDate;
	}

	public static NewsCommentResponse createResponse(NewsComment newsComment, Member member) {
		return NewsCommentResponse.builder()
			.newsCommentId(newsComment.getId())
			.newsId(newsComment.getNews().getId())
			.member(
				NewsCommentMemberResponse.builder()
					.memberPublicId(member.getPublicId())
					.nickName(member.getNickname())
					.build()
			)
			.comment(newsComment.getComment())
			.createDate(newsComment.getCreateDate())
			.build();
	}
}
