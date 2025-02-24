package org.myteam.server.news.newsCommentMember.service;

import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.newsComment.domain.NewsComment;
import org.myteam.server.news.newsComment.service.NewsCommentReadService;
import org.myteam.server.news.newsCommentMember.domain.NewsCommentMember;
import org.myteam.server.news.newsCommentMember.dto.service.response.NewsCommentMemberDeleteResponse;
import org.myteam.server.news.newsCommentMember.dto.service.response.NewsCommentMemberResponse;
import org.myteam.server.news.newsCommentMember.repository.NewsCommentMemberRepository;
import org.myteam.server.news.newsCountMember.dto.service.response.NewsCountMemberDeleteResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsCommentMemberService {

	private final NewsCommentMemberRepository newsCommentMemberRepository;
	private final SecurityReadService securityReadService;
	private final NewsCommentReadService newsCommentReadService;

	public NewsCommentMemberResponse save(Long newsCommentId) {
		Member member = securityReadService.getMember();
		NewsComment newsComment = newsCommentReadService.findById(newsCommentId);
		return NewsCommentMemberResponse.createResponse(
			newsCommentMemberRepository.save(NewsCommentMember.createEntity(newsComment, member))
		);
	}

	public NewsCommentMemberDeleteResponse deleteByNewsCommentIdMemberId(Long newsCommentId) {
		Member member = securityReadService.getMember();
		NewsComment newsComment = newsCommentReadService.findById(newsCommentId);

		newsCommentMemberRepository.deleteByNewsCommentIdAndMemberPublicId(newsComment.getId(), member.getPublicId());
		return NewsCommentMemberDeleteResponse.createResponse(newsComment.getId(), member.getPublicId());
	}

}
