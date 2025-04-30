package org.myteam.server.news.newsCountMember.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.news.newsCountMember.repository.NewsCountMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsCountMemberReadService {

    private final NewsCountMemberRepository newsCountMemberRepository;

    public void confirmExistMember(Long newsId, UUID memberId) {
        newsCountMemberRepository.findByNewsIdAndMemberPublicId(newsId, memberId)
                .ifPresent(member -> {
                    throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND_NEWS);
                });
    }

    public boolean confirmRecommendMember(Long newsId, UUID memberId) {
        return newsCountMemberRepository.findByNewsIdAndMemberPublicId(newsId, memberId)
                .isPresent();
    }

    public boolean isRecommended(Long newsId, UUID publicId) {
        return newsCountMemberRepository.findByNewsIdAndMemberPublicId(newsId, publicId).isPresent();
    }
}
