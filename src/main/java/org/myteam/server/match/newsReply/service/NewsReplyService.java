package org.myteam.server.match.newsReply.service;

import org.myteam.server.match.matchComment.domain.NewsComment;
import org.myteam.server.match.matchComment.service.NewsCommentReadService;
import org.myteam.server.match.newsReply.domain.NewsReply;
import org.myteam.server.match.newsReply.dto.service.request.NewsReplySaveServiceRequest;
import org.myteam.server.match.newsReply.dto.service.request.NewsReplyUpdateServiceRequest;
import org.myteam.server.match.newsReply.dto.service.response.NewsReplyResponse;
import org.myteam.server.match.newsReply.repository.NewsReplyRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsReplyService {

	private final NewsReplyRepository newsReplyRepository;
	private final NewsReplyReadService newsReplyReadService;
	private final NewsCommentReadService newsCommentReadService;
	private final SecurityReadService securityReadService;

	public NewsReplyResponse save(NewsReplySaveServiceRequest newsReplySaveServiceRequest) {
		NewsComment newsComment = newsCommentReadService.findById(newsReplySaveServiceRequest.getNewsCommentId());
		Member member = securityReadService.getMember();

		return NewsReplyResponse.createResponse(
			newsReplyRepository.save(
				NewsReply.createNewsReply(newsComment, member, newsReplySaveServiceRequest.getComment(),
					newsReplySaveServiceRequest.getIp())), member
		);
	}

	public Long update(NewsReplyUpdateServiceRequest newsReplyUpdateServiceRequest) {
		Member member = securityReadService.getMember();
		NewsReply newsReply = newsReplyReadService.findById(newsReplyUpdateServiceRequest.getNewsReplyId());

		newsReply.confirmMember(member);
		newsReply.updateComment(newsReplyUpdateServiceRequest.getComment());

		return newsReply.getId();
	}

	public Long delete(Long newsReplyId) {
		Member member = securityReadService.getMember();
		NewsReply newsReply = newsReplyReadService.findById(newsReplyId);

		newsReply.confirmMember(member);

		newsReplyRepository.deleteById(newsReplyId);
		return newsReply.getId();
	}
}
