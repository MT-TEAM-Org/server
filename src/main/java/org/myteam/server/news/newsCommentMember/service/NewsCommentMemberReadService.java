package org.myteam.server.news.newsCommentMember.service;

import java.util.UUID;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.news.newsCommentMember.repository.NewsCommentMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsCommentMemberReadService {

	private final NewsCommentMemberRepository newsCommentMemberRepository;

	public void confirmExistMember(Long newsId, UUID memberId) {
		newsCommentMemberRepository.findByNewsCommentIdAndMemberPublicId(newsId, memberId)
			.ifPresent(member -> {
				throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND_NEWS);
			});
	}

	public boolean confirmRecommendMember(Long newsId, UUID memberId) {
		return newsCommentMemberRepository.findByNewsCommentIdAndMemberPublicId(newsId, memberId)
			.isPresent();
	}

}
