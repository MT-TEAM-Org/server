package org.myteam.server.match.matchComment.dto.repository;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchCommentMemberDto {

	@Schema(description = "작성자 ID")
	private UUID publicId;
	@Schema(description = "작성자 닉네임")
	private String nickName;

	public MatchCommentMemberDto(UUID publicId, String nickName) {
		this.publicId = publicId;
		this.nickName = nickName;
	}
}
