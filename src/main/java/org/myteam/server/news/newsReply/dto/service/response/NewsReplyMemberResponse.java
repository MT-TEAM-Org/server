package org.myteam.server.news.newsReply.dto.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class NewsReplyMemberResponse {

	@Schema(description = "작성자 ID")
	private UUID publicId;
	@Schema(description = "작성자 닉네임")
	private String nickName;

	@Builder
	public NewsReplyMemberResponse(UUID publicId, String nickName) {
		this.publicId = publicId;
		this.nickName = nickName;
	}
}
