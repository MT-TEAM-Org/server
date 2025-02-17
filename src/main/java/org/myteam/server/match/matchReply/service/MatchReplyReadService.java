package org.myteam.server.match.matchReply.service;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.match.matchReply.domain.MatchReply;
import org.myteam.server.match.matchReply.dto.repository.MatchReplyDto;
import org.myteam.server.match.matchReply.dto.service.request.MatchReplyServiceRequest;
import org.myteam.server.match.matchReply.dto.service.response.MatchReplyListResponse;
import org.myteam.server.match.matchReply.repository.MatchReplyQueryRepository;
import org.myteam.server.match.matchReply.repository.MatchReplyRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchReplyReadService {

	private final MatchReplyRepository matchReplyRepository;
	private final MatchReplyQueryRepository matchReplyQueryRepository;

	public MatchReplyListResponse findByMatchCommentId(MatchReplyServiceRequest matchReplyServiceRequest) {
		Page<MatchReplyDto> list = matchReplyQueryRepository.getMatchReplyList(
			matchReplyServiceRequest.getMatchCommentId(),
			matchReplyServiceRequest.toPageable()
		);

		return MatchReplyListResponse.createResponse(PageCustomResponse.of(list));
	}

	public MatchReply findById(Long matchReplyId) {
		return matchReplyRepository.findById(matchReplyId)
			.orElseThrow(() -> new PlayHiveException(ErrorCode.MATCH_REPLY_NOT_FOUND));
	}

}
