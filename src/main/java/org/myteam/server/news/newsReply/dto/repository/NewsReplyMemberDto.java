package org.myteam.server.news.newsReply.dto.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplyMemberDto {
	private Long id;
	private String nickName;

	public NewsReplyMemberDto(Long id, String nickName) {
		this.id = id;
		this.nickName = nickName;
	}
}
