package org.myteam.server.match.matchReply.dto.service.request;

import org.myteam.server.global.page.request.PageInfoServiceRequest;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchReplyServiceRequest extends PageInfoServiceRequest {

	private Long matchCommentId;

	@Builder
	public MatchReplyServiceRequest(Long matchCommentId, int size, int page) {
		super(page, size);
		this.matchCommentId = matchCommentId;
	}
}
