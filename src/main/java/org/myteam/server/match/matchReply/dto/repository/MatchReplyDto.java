package org.myteam.server.match.matchReply.dto.repository;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchReplyDto {

	@Schema(description = "경기 대댓글 ID")
	private Long matchReplyId;
	@Schema(description = "경기 댓글 ID")
	private Long matchCommentId;
	@Schema(description = "경기 작성자")
	private MatchReplyMemberDto member;
	@Schema(description = "경기 대댓글 내용")
	private String comment;
	@Schema(description = "경기 대댓글 작성시 IP")
	private String ip;
	@Schema(description = "경기 대댓글 작성날짜")
	private LocalDateTime createTime;

	public MatchReplyDto(Long matchReplyId, Long matchCommentId, MatchReplyMemberDto member, String comment,
		String ip, LocalDateTime createTime) {
		this.matchReplyId = matchReplyId;
		this.matchCommentId = matchCommentId;
		this.member = member;
		this.comment = comment;
		this.ip = ip;
		this.createTime = createTime;
	}
}
