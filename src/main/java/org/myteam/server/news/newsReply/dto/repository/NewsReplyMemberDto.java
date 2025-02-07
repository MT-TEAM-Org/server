package org.myteam.server.news.newsReply.dto.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class NewsReplyMemberDto {
	private UUID publicId;
	private String nickName;

	public NewsReplyMemberDto(UUID publicId, String nickName) {
		this.publicId = publicId;
		this.nickName = nickName;
	}
}
