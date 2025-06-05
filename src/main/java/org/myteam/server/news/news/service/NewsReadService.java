package org.myteam.server.news.news.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.myteam.server.aop.CountView;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.service.RedisCountService;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.dto.repository.NewsDto;
import org.myteam.server.news.news.dto.service.request.NewsServiceRequest;
import org.myteam.server.news.news.dto.service.response.NewsListResponse;
import org.myteam.server.news.news.dto.service.response.NewsResponse;
import org.myteam.server.news.news.repository.NewsQueryRepository;
import org.myteam.server.news.news.repository.NewsRepository;
import org.myteam.server.news.newsCount.service.NewsCountReadService;
import org.myteam.server.news.newsCountMember.service.NewsCountMemberReadService;
import org.myteam.server.report.domain.DomainType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsReadService {

    private final NewsQueryRepository newsQueryRepository;
    private final NewsRepository newsRepository;
    private final NewsCountReadService newsCountReadService;
    private final NewsCountMemberReadService newsCountMemberReadService;
    private final SecurityReadService securityReadService;
    private final RedisCountService redisCountService;

    public NewsListResponse findAll(NewsServiceRequest newsServiceRequest) {
        Page<NewsDto> newsPagingList = newsQueryRepository.getNewsList(newsServiceRequest);

        return NewsListResponse.createResponse(PageCustomResponse.of(newsPagingList));
    }

    @CountView(domain = DomainType.NEWS, idParam = "newsId")
    public NewsResponse findOne(Long newsId) {
        UUID publicId = securityReadService.getAuthenticatedPublicId();

        boolean recommendYn = publicId != null && newsCountMemberReadService.confirmRecommendMember(newsId, publicId);

        News news = findById(newsId);
        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NEWS, newsId,
                null);

        Long previousId = newsQueryRepository.findPreviousNewsId(news.getId(), news.getCategory());
        Long nextId = newsQueryRepository.findNextNewsId(news.getId(), news.getCategory());

        return NewsResponse.createResponse(
                news,
                recommendYn,
                previousId,
                nextId, commonCountDto
        );
    }

    public News findById(Long newsId) {
        return newsRepository.findById(newsId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.NEWS_NOT_FOUND));
    }

    public boolean existsById(Long id) {
        return newsRepository.existsById(id);
    }

}
