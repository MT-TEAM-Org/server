package org.myteam.server.match.matchReply.dto.service.response;

import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.match.matchReply.dto.repository.MatchReplyDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchReplyListResponse {

	private PageCustomResponse<MatchReplyDto> list;

	@Builder
	public MatchReplyListResponse(PageCustomResponse<MatchReplyDto> list) {
		this.list = list;
	}

	public static MatchReplyListResponse createResponse(PageCustomResponse<MatchReplyDto> list) {
		return MatchReplyListResponse.builder()
			.list(list)
			.build();
	}
}
