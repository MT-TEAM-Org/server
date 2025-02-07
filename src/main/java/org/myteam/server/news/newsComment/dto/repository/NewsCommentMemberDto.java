package org.myteam.server.news.newsComment.dto.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class NewsCommentMemberDto {
	private UUID memberPublicId;
	private String nickName;

	public NewsCommentMemberDto(UUID memberPublicID, String nickName) {
		this.memberPublicId = memberPublicID;
		this.nickName = nickName;
	}
}
