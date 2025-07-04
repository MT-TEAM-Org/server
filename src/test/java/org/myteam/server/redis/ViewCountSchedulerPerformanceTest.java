package org.myteam.server.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.dto.request.CommentRequest;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.global.util.redis.ViewCountScheduler;
import org.myteam.server.global.util.redis.service.RedisCountService;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementCount;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.entity.MemberActivity;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.newsCount.domain.NewsCount;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.domain.NoticeCount;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.support.TestContainerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ViewCountSchedulerPerformanceTest extends TestContainerSupport {
    @Autowired
    private RedisCountService redisCountService;
    @Autowired
    private ViewCountScheduler viewCountScheduler;

    private Member member;
    private Board board;
    private BoardCount savedBoardCount;
    private News news;
    private NewsCount savedNewsCount;

    private Notice notice;
    private NoticeCount noticeCount;

    private Improvement improvement;
    private ImprovementCount improvementCount;

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

        board = createBoard(member, Category.BASEBALL, CategoryType.FREE, "야구 카테고리 제목", "야구 카테고리 내용");
        savedBoardCount = BoardCount.builder()
                .board(board)
                .build();

        news = createNews(1, Category.FOOTBALL, 0);

        notice = createNotice(member, "title", "content", "test@naver.com");
        noticeCount = NoticeCount.builder()
                .notice(notice)
                .build();

        improvement = createImprovement(member, false);
        improvementCount = ImprovementCount.builder()
                .improvement(improvement)
                .build();
    }

    @Test
    @DisplayName("비동기-동기 성능비고 테스트")
    void 동기_비동기_성능_비교_테스트() throws InterruptedException, ExecutionException{
        multiMemberConcurrentView();
        multiMemberConcurrentComment();
        multiMemberConcurrentRecommend();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 성능
        viewCountScheduler.updateCounts();

        stopWatch.stop();
        System.out.println("⏱ 총 걸린 시간(ms): " + stopWatch.getTotalTimeMillis());
    }

    private void multiMemberConcurrentView() throws InterruptedException, ExecutionException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(25);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        Member member = Member.builder()
                .email("test" + 1 + "@test.com")
                .password("1234")
                .tel("12345")
                .nickname("test")
                .role(MemberRole.USER)
                .type(MemberType.LOCAL)
                .publicId(UUID.randomUUID())
                .status(MemberStatus.ACTIVE)
                .build();

        executorService.submit(() -> {
            memberJpaRepository.save(member);
            boardRepository.save(board);
            boardCountRepository.save(savedBoardCount);
            newsRepository.save(news);
            noticeRepository.save(notice);
            noticeCountRepository.save(noticeCount);
            improvementRepository.save(improvement);
            improvementCountRepository.save(improvementCount);
        }).get();

        for (int i = 0; i < threadCount; i++) {
            /**
             * 해당 라인을 여기다 선언하면 동시성 이슈로 50보다 작은 값이 나옴
             */
//            redisCountService.getCommonCount(ServiceType.VIEW, DomainType.BOARD, board.getId(), null);
            executorService.execute(() -> {
                try {
                    redisCountService.getCommonCount(ServiceType.VIEW, DomainType.BOARD, board.getId(), null);
                    redisCountService.getCommonCount(ServiceType.VIEW, DomainType.NEWS, news.getId(), null);
                    redisCountService.getCommonCount(ServiceType.VIEW, DomainType.NOTICE, notice.getId(), null);
                    redisCountService.getCommonCount(ServiceType.VIEW, DomainType.IMPROVEMENT, improvement.getId(), null);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

    }

    private void multiMemberConcurrentRecommend() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        List<Member> members = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Member newMember = Member.builder()
                    .email("user" + i + "@test.com")
                    .password("1234")
                    .tel("010123456" + i)
                    .nickname("user" + i)
                    .role(MemberRole.USER)
                    .type(MemberType.LOCAL)
                    .publicId(UUID.randomUUID())
                    .status(MemberStatus.ACTIVE)
                    .build();
            memberJpaRepository.save(newMember);
            memberActivityRepository.save(new MemberActivity(newMember));
            members.add(newMember);
        }

        for (Member m : members) {
            executor.execute(() -> {
                try {
                    // 각 스레드마다 고유한 인증 정보 설정
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(new UsernamePasswordAuthenticationToken(
                            new CustomUserDetails(m),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    ));
                    SecurityContextHolder.setContext(context);

                    redisCountService.getCommonCount(ServiceType.RECOMMEND, DomainType.BOARD, board.getId(), null);
                    redisCountService.getCommonCount(ServiceType.RECOMMEND, DomainType.NEWS, news.getId(), null);
                    redisCountService.getCommonCount(ServiceType.RECOMMEND, DomainType.NOTICE, notice.getId(), null);
                    redisCountService.getCommonCount(ServiceType.RECOMMEND, DomainType.IMPROVEMENT, improvement.getId(), null);
                } catch (Exception e) {
                    System.err.println("❗예외 발생: " + e.getMessage());
                } finally {
                    SecurityContextHolder.clearContext();
                    latch.countDown();
                }
            });
        }

        latch.await();
    }

    private void multiMemberConcurrentComment() throws InterruptedException, ExecutionException {
        int threadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(25);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        Member member = Member.builder()
                .email("test" + 1 + "@test.com")
                .password("1234")
                .tel("12345")
                .nickname("test")
                .role(MemberRole.USER)
                .type(MemberType.LOCAL)
                .publicId(UUID.randomUUID())
                .status(MemberStatus.ACTIVE)
                .build();

        executorService.submit(() -> {
            memberJpaRepository.save(member);
            boardRepository.save(board);
            boardCountRepository.save(savedBoardCount);
            newsRepository.save(news);
            noticeRepository.save(notice);
            noticeCountRepository.save(noticeCount);
            improvementRepository.save(improvement);
            improvementCountRepository.save(improvementCount);
        }).get();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executorService.execute(() -> {
                try {
                    CommentRequest.CommentSaveRequest commentSaveRequest = CommentRequest.CommentSaveRequest.builder()
                            .mentionedPublicId(null)
                            .comment("댓글" + idx)
                            .imageUrl(null)
                            .type(CommentType.BOARD)
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

                    commentService.addComment(board.getId(), commentSaveRequest, "0.0.0.1");
                    commentService.addComment(news.getId(), commentSaveRequest, "0.0.0.1");
                    commentService.addComment(notice.getId(), commentSaveRequest, "0.0.0.1");
                    commentService.addComment(improvement.getId(), commentSaveRequest, "0.0.0.1");
                } finally {
                    SecurityContextHolder.clearContext();
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

    }
}
