package org.myteam.server.match.matchReply.dto.service.response;

import java.time.LocalDateTime;

import org.myteam.server.match.matchReply.domain.MatchReply;
import org.myteam.server.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchReplyResponse {

	@Schema(description = "경기 대댓글 ID")
	private Long matchReplyId;
	@Schema(description = "경기 댓글 ID")
	private Long matchCommentId;
	@Schema(description = "경기 작성자")
	private MatchReplyMemberResponse member;
	@Schema(description = "경기 대댓글 내용")
	private String comment;
	@Schema(description = "경기 대댓글 작성날짜")
	private LocalDateTime createDate;

	@Builder
	public MatchReplyResponse(Long matchReplyId, Long matchCommentId, MatchReplyMemberResponse member, String comment,
		LocalDateTime createDate) {
		this.matchReplyId = matchReplyId;
		this.matchCommentId = matchCommentId;
		this.member = member;
		this.comment = comment;
		this.createDate = createDate;
	}

	public static MatchReplyResponse createResponse(MatchReply matchReply, Member member) {
		return MatchReplyResponse.builder()
			.matchReplyId(matchReply.getId())
			.matchCommentId(matchReply.getMatchComment().getId())
			.member(
				MatchReplyMemberResponse.builder()
					.publicId(member.getPublicId())
					.nickName(member.getNickname())
					.build()
			)
			.comment(matchReply.getComment())
			.createDate(matchReply.getCreateDate())
			.build();
	}
}
