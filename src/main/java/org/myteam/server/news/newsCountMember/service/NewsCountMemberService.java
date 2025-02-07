package org.myteam.server.news.newsCountMember.service;

import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.service.NewsReadService;
import org.myteam.server.news.newsCountMember.domain.NewsCountMember;
import org.myteam.server.news.newsCountMember.dto.service.response.NewsCountMemberDeleteResponse;
import org.myteam.server.news.newsCountMember.dto.service.response.NewsCountMemberResponse;
import org.myteam.server.news.newsCountMember.repository.NewsCountMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsCountMemberService {

	private final NewsCountMemberRepository newsCountMemberRepository;
	private final SecurityReadService securityReadService;
	private final NewsReadService newsReadService;

	public NewsCountMemberResponse save(Long newsId) {
		Member member = securityReadService.getMember();
		News news = newsReadService.findById(newsId);
		return NewsCountMemberResponse.createResponse(
			newsCountMemberRepository.save(NewsCountMember.createEntity(news, member))
		);
	}

	public NewsCountMemberDeleteResponse deleteByNewsIdMemberId(Long newsId) {
		Member member = securityReadService.getMember();
		News news = newsReadService.findById(newsId);

		newsCountMemberRepository.deleteByNewsIdAndMemberPublicId(news.getId(), member.getPublicId());
		return NewsCountMemberDeleteResponse.createResponse(news.getId(), member.getPublicId());
	}

}
