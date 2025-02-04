package org.myteam.server.news.service;

import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.dto.service.request.NewsCountMemberSaveServiceRequest;
import org.myteam.server.news.dto.service.response.NewsCountMemberResponse;
import org.myteam.server.news.repository.NewsCountMemberRepository;
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

	public NewsCountMemberResponse save(NewsCountMemberSaveServiceRequest newsCountMemberSaveServiceRequest) {
		return NewsCountMemberResponse.createResponse(
			newsCountMemberRepository.save(
				newsCountMemberSaveServiceRequest.toEntity(
					newsReadService.findById(newsCountMemberSaveServiceRequest.getNewsId()),
					securityReadService.getMember()
				)
			)
		);
	}

}
