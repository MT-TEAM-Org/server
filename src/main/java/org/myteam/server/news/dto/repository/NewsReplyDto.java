package org.myteam.server.news.dto.repository;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplyDto {

	private Long newsReplyId;
	private Long newsCommentId;
	private NewsReplyMemberDto memberDto;
	private String comment;
	private String ip;
	private LocalDateTime createTime;

	public NewsReplyDto(Long newsReplyId, Long newsCommentId, NewsReplyMemberDto memberDto, String comment,
		String ip, LocalDateTime createTime) {
		this.newsReplyId = newsReplyId;
		this.newsCommentId = newsCommentId;
		this.memberDto = memberDto;
		this.comment = comment;
		this.ip = ip;
		this.createTime = createTime;
	}
}
