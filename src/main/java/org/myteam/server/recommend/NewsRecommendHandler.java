package org.myteam.server.recommend;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.repository.NewsRepository;
import org.myteam.server.news.newsCountMember.domain.NewsCountMember;
import org.myteam.server.news.newsCountMember.repository.NewsCountMemberRepository;
import org.myteam.server.news.newsCountMember.service.NewsCountMemberReadService;
import org.myteam.server.report.domain.DomainType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsRecommendHandler implements RecommendHandler {

    private final NewsCountMemberReadService newsCountMemberReadService;
    private final NewsCountMemberRepository newsCountMemberRepository;
    private final NewsRepository newsRepository;

    @Override
    public boolean supports(DomainType type) {
        return type.name().equalsIgnoreCase("news");
    }

    @Override
    public boolean isAlreadyRecommended(Long contentId, UUID userId) {
        // Redis Set 조회나 DB 조회
        return newsCountMemberReadService.isRecommended(contentId, userId);
    }

    @Override
    public void saveRecommendation(Long contentId, Member member) {
        // 저장 또는 큐에 넣기
        News news = newsRepository.findById(contentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.NEWS_NOT_FOUND));
        NewsCountMember recommend = NewsCountMember.builder().news(news).member(member).build();
        newsCountMemberRepository.save(recommend);
    }

    @Override
    public void deleteRecommendation(Long contentId, UUID userId) {
        newsCountMemberRepository.deleteByNewsIdAndMemberPublicId(contentId, userId);
    }
}
