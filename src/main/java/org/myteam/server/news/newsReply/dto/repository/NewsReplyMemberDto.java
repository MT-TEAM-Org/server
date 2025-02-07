package org.myteam.server.news.newsReply.dto.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class NewsReplyMemberDto {
	private UUID memberPublicId;
	private String nickName;

	public NewsReplyMemberDto(UUID memberPublicId, String nickName) {
		this.memberPublicId = memberPublicId;
		this.nickName = nickName;
	}
}
