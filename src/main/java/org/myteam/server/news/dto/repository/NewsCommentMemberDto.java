package org.myteam.server.news.dto.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentMemberDto {
	private Long id;
	private String nickName;

	public NewsCommentMemberDto(Long id, String nickName) {
		this.id = id;
		this.nickName = nickName;
	}
}
