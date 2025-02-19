package org.myteam.server.match.matchComment.dto.controller.request;

import org.myteam.server.match.matchComment.dto.service.request.MatchCommentUpdateServiceRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchCommentUpdateRequest {

	@Schema(description = "경기 댓글 ID")
	@NotNull(message = "경기 댓글 ID는 필수입니다.")
	private Long matchCommentId;
	@Schema(description = "뉴스 댓글 내용")
	@NotNull(message = "경기 댓글 내용은 필수입니다.")
	private String comment;
	@Schema(description = "경기 댓글 이미지 (삭제시 null)")
	private String imgUrl;

	@Builder
	public MatchCommentUpdateRequest(Long matchCommentId, String comment, String imgUrl) {
		this.matchCommentId = matchCommentId;
		this.comment = comment;
		this.imgUrl = imgUrl;
	}

	public MatchCommentUpdateServiceRequest toServiceRequest() {
		return MatchCommentUpdateServiceRequest.builder()
			.matchCommentId(matchCommentId)
			.comment(comment)
			.imgUrl(imgUrl)
			.build();
	}
}
