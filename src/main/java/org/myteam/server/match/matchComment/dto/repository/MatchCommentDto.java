package org.myteam.server.match.matchComment.dto.repository;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchCommentDto {

	@Schema(description = "경기 댓글 ID")
	private Long matchCommentId;
	@Schema(description = "경기 ID")
	private Long matchId;
	@Schema(description = "경기 댓글 작성자")
	private MatchCommentMemberDto memberDto;
	@Schema(description = "경기 댓글")
	private String comment;
	@Schema(description = "경기 댓글 작성시 IP")
	private String ip;
	@Schema(description = "경기 댓글 날짜")
	private LocalDateTime createTime;

	public MatchCommentDto(Long matchCommentId, Long matchId, MatchCommentMemberDto memberDto, String comment, String ip, LocalDateTime createTime) {
		this.matchCommentId = matchCommentId;
		this.matchId = matchId;
		this.memberDto = memberDto;
		this.comment = comment;
		this.ip = ip;
		this.createTime = createTime;
	}
}
