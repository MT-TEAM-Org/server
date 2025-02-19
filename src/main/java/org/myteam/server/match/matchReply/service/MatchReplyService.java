package org.myteam.server.match.matchReply.service;

import org.myteam.server.match.matchComment.domain.MatchComment;
import org.myteam.server.match.matchComment.service.MatchCommentReadService;
import org.myteam.server.match.matchReply.domain.MatchReply;
import org.myteam.server.match.matchReply.dto.service.request.MatchReplySaveServiceRequest;
import org.myteam.server.match.matchReply.dto.service.request.MatchReplyUpdateServiceRequest;
import org.myteam.server.match.matchReply.dto.service.response.MatchReplyResponse;
import org.myteam.server.match.matchReply.repository.MatchReplyRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchReplyService {

	private final MatchReplyRepository matchReplyRepository;
	private final MatchReplyReadService matchReplyReadService;
	private final MatchCommentReadService matchCommentReadService;
	private final SecurityReadService securityReadService;

	public MatchReplyResponse save(MatchReplySaveServiceRequest matchReplySaveServiceRequest) {
		MatchComment matchComment = matchCommentReadService.findById(matchReplySaveServiceRequest.getMatchCommentId());
		Member member = securityReadService.getMember();

		return MatchReplyResponse.createResponse(
			matchReplyRepository.save(
				MatchReply.createEntity(matchComment, member, matchReplySaveServiceRequest.getComment(),
					matchReplySaveServiceRequest.getIp())), member
		);
	}

	public Long update(MatchReplyUpdateServiceRequest matchReplyUpdateServiceRequest) {
		Member member = securityReadService.getMember();
		MatchReply matchReply = matchReplyReadService.findById(matchReplyUpdateServiceRequest.getMatchReplyId());

		matchReply.confirmMember(member);
		matchReply.updateComment(matchReplyUpdateServiceRequest.getComment());

		return matchReply.getId();
	}

	public Long delete(Long matchReplyId) {
		Member member = securityReadService.getMember();
		MatchReply matchReply = matchReplyReadService.findById(matchReplyId);

		matchReply.confirmMember(member);

		matchReplyRepository.deleteById(matchReplyId);
		return matchReply.getId();
	}
}
