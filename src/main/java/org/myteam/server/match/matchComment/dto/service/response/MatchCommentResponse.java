package org.myteam.server.match.matchComment.dto.service.response;

import java.time.LocalDateTime;

import org.myteam.server.match.matchComment.domain.MatchComment;
import org.myteam.server.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchCommentResponse {

	@Schema(description = "경기 댓글 ID")
	private Long matchCommentId;
	@Schema(description = "경기 ID")
	private Long matchId;
	@Schema(description = "경기 댓글 작성자")
	private MatchCommentMemberResponse member;
	@Schema(description = "경기 댓글 내용")
	private String comment;
	@Schema(description = "경기 댓글 작성 날짜")
	private LocalDateTime createDate;

	@Builder
	public MatchCommentResponse(Long matchCommentId, Long matchId, MatchCommentMemberResponse member, String comment,
		LocalDateTime createDate) {
		this.matchCommentId = matchCommentId;
		this.matchId = matchId;
		this.member = member;
		this.comment = comment;
		this.createDate = createDate;
	}

	public static MatchCommentResponse createResponse(MatchComment matchComment, Member member) {
		return MatchCommentResponse.builder()
			.matchCommentId(matchComment.getId())
			.matchId(matchComment.getMatch().getId())
			.member(
				MatchCommentMemberResponse.builder()
					.memberPublicId(member.getPublicId())
					.nickName(member.getNickname())
					.build()
			)
			.comment(matchComment.getComment())
			.createDate(matchComment.getCreateDate())
			.build();
	}
}
