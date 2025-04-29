package org.myteam.server.news.newsCount.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.TestContainerSupport;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.dto.request.CommentRequest;
import org.myteam.server.comment.dto.response.CommentResponse;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.RedisCountService;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.entity.MemberActivity;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.newsCount.domain.NewsCount;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

public class NewsCountServiceTest extends TestContainerSupport {

    @Autowired
    private RedisCountService redisCountService;
    @Autowired
    private NewsCountService newsCountService;

    private Member member;

    @BeforeEach
    public void setUp() {
        member = Member.builder()
                .email("test@test.com")
                .password("1234")
                .tel("12345")
                .nickname("test")
                .role(MemberRole.USER)
                .type(MemberType.LOCAL)
                .publicId(UUID.randomUUID())
                .status(MemberStatus.ACTIVE)
                .build();

        memberJpaRepository.save(member);
        MemberActivity memberActivity = new MemberActivity(member);
        memberActivityRepository.save(memberActivity);

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(member),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        ));
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    public void cleanUp() {
        commentRepository.deleteAllInBatch();
        newsCountMemberRepository.deleteAllInBatch();
        newsCountRepository.deleteAllInBatch();
        newsRepository.deleteAllInBatch();
    }

    @DisplayName("뉴스 추천 클릭시 사용자 좋아요 추가를 테스트한다.")
    @Test
    @Transactional
    void recommendNewsTest() {
        // given
        News news = createNews(1, Category.FOOTBALL, 0);

        // when
        newsCountService.recommendNews(news.getId());

        // then
        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NEWS,
                news.getId(), null);
        System.out.println("commonCountDto.getRecommendCount( = " + commonCountDto.getRecommendCount());

        assertThat(commonCountDto.getRecommendCount()).isEqualTo(1);
    }

    @DisplayName("뉴스 추천 취소시 사용자 좋아요 제거를 테스트한다.")
    @Test
    @Transactional
    void cancelRecommendNewsTest() {
        // given
        News news = createNews(1, Category.FOOTBALL, 0);
        newsCountService.recommendNews(news.getId());

        // when
        newsCountService.cancelRecommendNews(news.getId());

        //then
        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NEWS,
                news.getId(), null);
        System.out.println("commonCountDto.getRecommendCount( = " + commonCountDto.getRecommendCount());

        assertThat(commonCountDto.getRecommendCount()).isEqualTo(0);
    }

    @DisplayName("뉴스 추천수 증가 동시성 테스트한다.")
    @Test
    void addRecommendCountTest() throws InterruptedException, ExecutionException {
        int threadCount = 50;

        ExecutorService executorService = Executors.newFixedThreadPool(25);

        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        News news = News.builder()
                .title("기사타이틀" + 1)
                .category(Category.FOOTBALL)
                .thumbImg("www.test.com")
                .build();

        NewsCount savedNewsCount = NewsCount.builder()
                .news(news)
                .build();

        executorService.submit(() -> {
            newsRepository.save(news);
            newsCountRepository.save(savedNewsCount);
        }).get();

        redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NEWS, news.getId(), null);

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executorService.execute(() -> {
                try {
                    Member threadMember = Member.builder()
                            .email("test" + idx + "@test.com")
                            .password("1234")
                            .tel("01012345678")
                            .nickname("test" + idx)
                            .role(MemberRole.USER)
                            .type(MemberType.LOCAL)
                            .status(MemberStatus.ACTIVE)
                            .publicId(UUID.randomUUID())
                            .build();
                    memberJpaRepository.save(threadMember);

                    // member로 로그인 시큐리티 컨텍스트 세팅 필요시 여기에
                    MemberActivity threadMemberActivity = new MemberActivity(threadMember);
                    memberActivityRepository.save(threadMemberActivity);

                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(new UsernamePasswordAuthenticationToken(
                            new CustomUserDetails(threadMember),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    ));
                    SecurityContextHolder.setContext(context);

                    newsCountService.recommendNews(news.getId());
                } finally {
                    SecurityContextHolder.clearContext();
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NEWS,
                news.getId(), null);
        System.out.println("commonCountDto.getRecommendCount( = " + commonCountDto.getRecommendCount());

        assertThat(commonCountDto.getRecommendCount()).isEqualTo(50);
    }

    @DisplayName("뉴스 추천수 감소 동시성 테스트한다.")
    @Test
    void minusRecommendCountTest() throws InterruptedException, ExecutionException {
        int threadCount = 50;

        ExecutorService executorService = Executors.newFixedThreadPool(25);
        CountDownLatch recommendLatch = new CountDownLatch(threadCount);
        CountDownLatch cancelLatch = new CountDownLatch(threadCount);

        ConcurrentHashMap<Integer, Member> memberMap = new ConcurrentHashMap<>();

        News news = News.builder()
                .title("기사타이틀" + 1)
                .category(Category.FOOTBALL)
                .thumbImg("www.test.com")
                .build();

        NewsCount savedNewsCount = NewsCount.builder()
                .news(news)
                .recommendCount(0)
                .build();

        executorService.submit(() -> {
            newsRepository.save(news);
            newsCountRepository.save(savedNewsCount);
        }).get();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executorService.execute(() -> {
                try {
                    Member threadMember = Member.builder()
                            .email("test" + idx + "@test.com")
                            .password("1234")
                            .tel("01012345678")
                            .nickname("test" + idx)
                            .role(MemberRole.USER)
                            .type(MemberType.LOCAL)
                            .status(MemberStatus.ACTIVE)
                            .publicId(UUID.randomUUID())
                            .build();
                    memberJpaRepository.save(threadMember);
                    memberMap.put(idx, threadMember);

                    // member로 로그인 시큐리티 컨텍스트 세팅 필요시 여기에
                    MemberActivity threadMemberActivity = new MemberActivity(threadMember);
                    memberActivityRepository.save(threadMemberActivity);

                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(new UsernamePasswordAuthenticationToken(
                            new CustomUserDetails(threadMember),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    ));
                    SecurityContextHolder.setContext(context);

                    newsCountService.recommendNews(news.getId());
                } finally {
                    SecurityContextHolder.clearContext();
                    recommendLatch.countDown();
                }
            });
        }
        recommendLatch.await();

        // when
        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executorService.execute(() -> {
                try {
                    Member threadMember = memberMap.get(idx);

                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(new UsernamePasswordAuthenticationToken(
                            new CustomUserDetails(threadMember),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    ));
                    SecurityContextHolder.setContext(context);

                    newsCountService.cancelRecommendNews(news.getId());
                } finally {
                    SecurityContextHolder.clearContext();
                    cancelLatch.countDown();
                }
            });
        }
        cancelLatch.await();

        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NEWS,
                news.getId(), null);
        assertThat(commonCountDto.getRecommendCount()).isEqualTo(0);
    }

    @DisplayName("뉴스 댓글수 증가 동시성 테스트한다.")
    @Test
    void addCommentCountTest() throws InterruptedException, ExecutionException {
        int threadCount = 50;

        ExecutorService executorService = Executors.newFixedThreadPool(25);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        News news = News.builder()
                .title("기사타이틀" + 1)
                .category(Category.FOOTBALL)
                .thumbImg("www.test.com")
                .build();

        NewsCount savedNewsCount = NewsCount.builder()
                .news(news)
                .build();

        executorService.submit(() -> {
            newsRepository.save(news);
            newsCountRepository.save(savedNewsCount);
        }).get();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executorService.execute(() -> {
                try {
                    CommentRequest.CommentSaveRequest commentSaveRequest = CommentRequest.CommentSaveRequest.builder()
                            .mentionedPublicId(null)
                            .comment("댓글" + idx)
                            .imageUrl(null)
                            .type(CommentType.NEWS)
                            .parentId(null)
                            .build();

                    Member threadMember = Member.builder()
                            .email("test" + idx + "@test.com")
                            .password("1234")
                            .tel("01012345678")
                            .nickname("test" + idx)
                            .role(MemberRole.USER)
                            .type(MemberType.LOCAL)
                            .status(MemberStatus.ACTIVE)
                            .publicId(UUID.randomUUID())
                            .build();
                    memberJpaRepository.save(threadMember);

                    // member로 로그인 시큐리티 컨텍스트 세팅 필요시 여기에
                    MemberActivity threadMemberActivity = new MemberActivity(threadMember);
                    memberActivityRepository.save(threadMemberActivity);

                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(new UsernamePasswordAuthenticationToken(
                            new CustomUserDetails(threadMember),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    ));
                    SecurityContextHolder.setContext(context);

                    commentService.addComment(news.getId(), commentSaveRequest, "0.0.0.1");
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NEWS,
                news.getId(), null);
        assertThat(commonCountDto.getCommentCount()).isLessThanOrEqualTo(50);
    }

    @DisplayName("뉴스 댓글수 감소 동시성 테스트한다.")
    @Test
    void minusCommentCountTest() throws InterruptedException, ExecutionException {
        int threadCount = 50;

        ExecutorService executorService = Executors.newFixedThreadPool(25);
        CountDownLatch commentLatch = new CountDownLatch(threadCount);
        CountDownLatch deleteLatch = new CountDownLatch(threadCount);

        News news = News.builder()
                .title("기사타이틀" + 1)
                .category(Category.FOOTBALL)
                .thumbImg("www.test.com")
                .build();

        NewsCount savedNewsCount = NewsCount.builder()
                .news(news)
                .commentCount(0)
                .build();

        executorService.submit(() -> {
            newsRepository.save(news);
            newsCountRepository.save(savedNewsCount);
        }).get();

        ConcurrentHashMap<Integer, Member> memberMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, Long> commentMap = new ConcurrentHashMap<>();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executorService.execute(() -> {
                try {
                    CommentRequest.CommentSaveRequest commentSaveRequest = CommentRequest.CommentSaveRequest.builder()
                            .mentionedPublicId(null)
                            .comment("댓글" + idx)
                            .imageUrl(null)
                            .type(CommentType.NEWS)
                            .parentId(null)
                            .build();
                    Member threadMember = Member.builder()
                            .email("test" + idx + "@test.com")
                            .password("1234")
                            .tel("01012345678")
                            .nickname("test" + idx)
                            .role(MemberRole.USER)
                            .type(MemberType.LOCAL)
                            .status(MemberStatus.ACTIVE)
                            .publicId(UUID.randomUUID())
                            .build();
                    memberJpaRepository.save(threadMember);
                    memberMap.put(idx, threadMember);

                    // member로 로그인 시큐리티 컨텍스트 세팅 필요시 여기에
                    MemberActivity threadMemberActivity = new MemberActivity(threadMember);
                    memberActivityRepository.save(threadMemberActivity);

                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(new UsernamePasswordAuthenticationToken(
                            new CustomUserDetails(threadMember),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    ));
                    SecurityContextHolder.setContext(context);

                    CommentResponse.CommentSaveResponse comment = commentService.addComment(news.getId(),
                            commentSaveRequest, "0.0.0.1");
                    commentMap.put(idx, comment.getCommentId());
                } finally {
                    commentLatch.countDown();
                }
            });
        }
        commentLatch.await();

        CommentRequest.CommentDeleteRequest deleteRequest = CommentRequest.CommentDeleteRequest.builder()
                .type(CommentType.NEWS)
                .build();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executorService.execute(() -> {
                try {
                    Member threadMember = memberMap.get(idx);

                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(new UsernamePasswordAuthenticationToken(
                            new CustomUserDetails(threadMember),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    ));
                    SecurityContextHolder.setContext(context);
                    System.out.println("commentMap.get(idx) = " + commentMap.get(idx));
                    commentService.deleteComment(news.getId(), commentMap.get(idx), deleteRequest);
                } finally {
                    deleteLatch.countDown();
                }
            });
        }
        deleteLatch.await();

        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NEWS,
                news.getId(), null);

        assertThat(commonCountDto.getCommentCount()).isLessThanOrEqualTo(0);
    }

    @DisplayName("뉴스 조회수 증가 동시성 테스트한다.")
    @Test
    void addViewCountTest() throws InterruptedException, ExecutionException {
        int threadCount = 50;

        ExecutorService executorService = Executors.newFixedThreadPool(25);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        News news = News.builder()
                .title("기사타이틀" + 1)
                .category(Category.FOOTBALL)
                .thumbImg("www.test.com")
                .build();

        NewsCount savedNewsCount = NewsCount.builder()
                .news(news)
                .build();

        executorService.submit(() -> {
            newsRepository.save(news);
            newsCountRepository.save(savedNewsCount);
        }).get();

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    redisCountService.getCommonCount(ServiceType.VIEW, DomainType.NEWS, news.getId(), null);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NEWS,
                news.getId(), null);

        assertThat(commonCountDto.getViewCount()).isLessThanOrEqualTo(50);
    }

    @Transactional
    @Test
    @DisplayName("뉴스 추천 처리 시 RECOMMEND 요청이 RedisCountService에 전달된다.")
    void recommendNews_호출시_RECOMMEND() {
        // given
        newsCountRepository.deleteAllInBatch();
        newsRepository.deleteAllInBatch();
        News news = createNews(1, Category.FOOTBALL, 1);

        // when
        newsCountService.recommendNews(news.getId());

        // then
        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NEWS, news.getId(), null);
        assertThat(commonCountDto.getRecommendCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("뉴스 추천 취소 처리 시 RECOMMEND_CANCEL 요청이 RedisCountService에 전달된다.")
    void cancelRecommendNews_호출시_RECOMMEND_CANCEL() {
        // given
        News news = createNews(1, Category.FOOTBALL, 1);
        newsCountService.recommendNews(news.getId());

        // when
        newsCountService.cancelRecommendNews(news.getId());

        // then
        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NEWS, news.getId(), null);
        assertThat(commonCountDto.getRecommendCount()).isEqualTo(1);
    }
}