package org.myteam.server.match.matchComment.service;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.match.matchComment.domain.MatchComment;
import org.myteam.server.match.matchComment.dto.repository.MatchCommentDto;
import org.myteam.server.match.matchComment.dto.service.request.MatchCommentServiceRequest;
import org.myteam.server.match.matchComment.dto.service.response.MatchCommentListResponse;
import org.myteam.server.match.matchComment.repository.MatchCommentLockRepository;
import org.myteam.server.match.matchComment.repository.MatchCommentQueryRepository;
import org.myteam.server.match.matchComment.repository.MatchCommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchCommentReadService {

	private final MatchCommentRepository matchCommentRepository;
	private final MatchCommentQueryRepository matchCommentQueryRepository;
	private final MatchCommentLockRepository matchCommentLockRepository;

	public MatchCommentListResponse findByMatchId(MatchCommentServiceRequest matchCommentServiceRequest) {

		Page<MatchCommentDto> list = matchCommentQueryRepository.getNewsCommentList(
			matchCommentServiceRequest.getMatchId(),
			matchCommentServiceRequest.toPageable()
		);

		return MatchCommentListResponse.createResponse(PageCustomResponse.of(list));
	}

	public MatchComment findById(Long newsCommentId) {
		return matchCommentRepository.findById(newsCommentId)
			.orElseThrow(() -> new PlayHiveException(ErrorCode.MATCH_COMMENT_NOT_FOUND));
	}

	public MatchComment findByIdLock(Long newsCommentId) {
		return matchCommentLockRepository.findById(newsCommentId)
			.orElseThrow(() -> new PlayHiveException(ErrorCode.MATCH_COMMENT_NOT_FOUND));
	}

}
