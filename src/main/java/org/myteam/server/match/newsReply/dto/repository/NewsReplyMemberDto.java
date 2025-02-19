package org.myteam.server.match.newsReply.dto.repository;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplyMemberDto {

	@Schema(description = "작성자 ID")
	private UUID publicId;
	@Schema(description = "작성자 닉네임")
	private String nickName;

	public NewsReplyMemberDto(UUID publicId, String nickName) {
		this.publicId = publicId;
		this.nickName = nickName;
	}
}
