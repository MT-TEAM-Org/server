package org.myteam.server.news.dto.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentDto {

	private Long newsCommentId;
	private Long newsId;
	private NewsCommentMemberDto memberDto;
	private String comment;
	private String ip;

	public NewsCommentDto(Long newsCommentId, Long newsId, NewsCommentMemberDto memberDto, String comment, String ip) {
		this.newsCommentId = newsCommentId;
		this.newsId = newsId;
		this.memberDto = memberDto;
		this.comment = comment;
		this.ip = ip;
	}
}
