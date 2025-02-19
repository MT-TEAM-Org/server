package org.myteam.server.news.newsReplyMember.service;

import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.newsReply.domain.NewsReply;
import org.myteam.server.news.newsReply.service.NewsReplyReadService;
import org.myteam.server.news.newsReplyMember.domain.NewsReplyMember;
import org.myteam.server.news.newsReplyMember.dto.service.response.NewsReplyMemberDeleteResponse;
import org.myteam.server.news.newsReplyMember.dto.service.response.NewsReplyMemberResponse;
import org.myteam.server.news.newsReplyMember.repository.NewsReplyMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsReplyMemberService {

	private final NewsReplyMemberRepository newsReplyMemberRepository;
	private final SecurityReadService securityReadService;
	private final NewsReplyReadService newsReplyReadService;

	public NewsReplyMemberResponse save(Long newsReplyId) {
		Member member = securityReadService.getMember();
		NewsReply newsReply = newsReplyReadService.findById(newsReplyId);
		return NewsReplyMemberResponse.createResponse(
			newsReplyMemberRepository.save(NewsReplyMember.createEntity(newsReply, member))
		);
	}

	public NewsReplyMemberDeleteResponse deleteByNewsReplyIdMemberId(Long newsReplyId) {
		Member member = securityReadService.getMember();
		NewsReply newsReply = newsReplyReadService.findById(newsReplyId);

		newsReplyMemberRepository.deleteByNewsReplyIdAndMemberPublicId(newsReply.getId(), member.getPublicId());
		return NewsReplyMemberDeleteResponse.createResponse(newsReply.getId(), member.getPublicId());
	}

}
