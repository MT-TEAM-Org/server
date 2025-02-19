package org.myteam.server.news.newsReplyMember.service;

import java.util.UUID;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.news.newsReplyMember.repository.NewsReplyMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsReplyMemberReadService {

	private final NewsReplyMemberRepository newsReplyMemberRepository;

	public void confirmExistMember(Long newsId, UUID memberId) {
		newsReplyMemberRepository.findByNewsReplyIdAndMemberPublicId(newsId, memberId)
			.ifPresent(member -> {
				throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND_NEWS);
			});
	}

	public boolean confirmRecommendMember(Long newsId, UUID memberId) {
		return newsReplyMemberRepository.findByNewsReplyIdAndMemberPublicId(newsId, memberId)
			.isPresent();
	}

}
