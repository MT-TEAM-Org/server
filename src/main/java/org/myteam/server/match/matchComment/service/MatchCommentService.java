package org.myteam.server.match.matchComment.service;

import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.service.MatchReadService;
import org.myteam.server.match.matchComment.domain.MatchComment;
import org.myteam.server.match.matchComment.dto.service.request.MatchCommentSaveServiceRequest;
import org.myteam.server.match.matchComment.dto.service.request.MatchCommentUpdateServiceRequest;
import org.myteam.server.match.matchComment.dto.service.response.MatchCommentResponse;
import org.myteam.server.match.matchComment.repository.MatchCommentRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchCommentService {

	private final MatchCommentRepository matchCommentRepository;
	private final MatchCommentReadService matchCommentReadService;
	private final MatchReadService matchReadService;
	private final SecurityReadService securityReadService;
	// private final MatchCountService newsCountService;

	public MatchCommentResponse save(MatchCommentSaveServiceRequest matchCommentSaveServiceRequest) {
		Match match = matchReadService.findById(matchCommentSaveServiceRequest.getMatchId());
		Member member = securityReadService.getMember();

		MatchComment matchComment = matchCommentRepository.save(
			MatchComment.createEntity(match, member, matchCommentSaveServiceRequest.getComment(),
				matchCommentSaveServiceRequest.getIp(), matchCommentSaveServiceRequest.getImgUrl()));

		// newsCountService.addCommendCount(matchComment.getId());

		return MatchCommentResponse.createResponse(matchComment, member);
	}

	public Long update(MatchCommentUpdateServiceRequest matchCommentUpdateServiceRequest) {
		Member member = securityReadService.getMember();
		MatchComment matchComment = matchCommentReadService.findById(matchCommentUpdateServiceRequest.getMatchCommentId());

		matchComment.confirmMember(member);
		matchComment.update(matchCommentUpdateServiceRequest.getComment(), matchCommentUpdateServiceRequest.getImgUrl());

		return matchComment.getId();
	}

	public Long delete(Long matchCommentId) {
		Member member = securityReadService.getMember();
		MatchComment matchComment = matchCommentReadService.findById(matchCommentId);

		matchComment.confirmMember(member);

		matchCommentRepository.deleteById(matchCommentId);

		// match.minusCommendCount(newsComment.getNews().getId());

		return matchComment.getId();
	}

	public Long recommendComment(Long matchCommentId) {
		Member member = securityReadService.getMember();
		MatchComment matchComment = matchCommentReadService.findByIdLock(matchCommentId);

		matchComment.addRecommendCount();
		return matchComment.getId();
	}
}
