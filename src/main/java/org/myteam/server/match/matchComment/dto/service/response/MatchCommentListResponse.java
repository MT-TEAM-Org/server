package org.myteam.server.match.matchComment.dto.service.response;

import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.match.matchComment.dto.repository.MatchCommentDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchCommentListResponse {

	private PageCustomResponse<MatchCommentDto> list;

	@Builder
	public MatchCommentListResponse(PageCustomResponse<MatchCommentDto> list) {
		this.list = list;
	}

	public static MatchCommentListResponse createResponse(PageCustomResponse<MatchCommentDto> list) {
		return MatchCommentListResponse.builder()
			.list(list)
			.build();
	}
}
