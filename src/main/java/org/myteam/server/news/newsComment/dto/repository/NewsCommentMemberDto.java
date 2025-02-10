package org.myteam.server.news.newsComment.dto.repository;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class NewsCommentMemberDto {

	@Schema(description = "작성자 ID")
	private UUID publicId;
	@Schema(description = "작성자 닉네임")
	private String nickName;

	public NewsCommentMemberDto(UUID publicId, String nickName) {
		this.publicId = publicId;
		this.nickName = nickName;
	}
}
