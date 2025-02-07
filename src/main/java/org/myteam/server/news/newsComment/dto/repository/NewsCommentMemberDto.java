package org.myteam.server.news.newsComment.dto.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class NewsCommentMemberDto {
	private UUID publicId;
	private String nickName;

	public NewsCommentMemberDto(UUID publicId, String nickName) {
		this.publicId = publicId;
		this.nickName = nickName;
	}
}
