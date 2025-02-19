package org.myteam.server.match.matchComment.dto.controller.request;

import org.myteam.server.match.matchComment.dto.service.request.MatchCommentSaveServiceRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchCommentSaveRequest {

	@Schema(description = "경기 ID")
	@NotNull(message = "경기ID는 필수입니다.")
	private Long matchId;
	@Schema(description = "댓글")
	@NotNull(message = "경기 댓글은 필수입니다.")
	private String comment;
	@Schema(description = "경기 댓글 이미지 (있을 시)")
	private String imgUrl;

	@Builder
	public MatchCommentSaveRequest(Long matchId, String comment, String imgUrl) {
		this.matchId = matchId;
		this.comment = comment;
		this.imgUrl = imgUrl;
	}

	public MatchCommentSaveServiceRequest toServiceRequest(String ip) {
		return MatchCommentSaveServiceRequest.builder()
			.matchId(matchId)
			.comment(comment)
			.ip(ip)
			.imgUrl(imgUrl)
			.build();
	}
}
