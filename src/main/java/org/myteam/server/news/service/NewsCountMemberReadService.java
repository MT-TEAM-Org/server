package org.myteam.server.news.service;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.news.repository.NewsCountMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsCountMemberReadService {

	private final NewsCountMemberRepository newsCountMemberRepository;

	public void confirmExistMember(Long newsId, Long memberId) {
		newsCountMemberRepository.findByNewsIdAndMemberId(newsId, memberId)
			.ifPresent(member -> {
				throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND_NEWS);
			});
	}

}
