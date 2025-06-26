package org.myteam.server.RedisRedissonTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.global.util.redis.service.RedisCountService;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.entity.MemberActivity;
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

/**
 * Redisson을 사용햇을 때와 Redis만 사용했을때 추천 기능의 정합성과 시간을 비교하기 위한 테스트 코드
 */
@SpringBootTest
@ActiveProfiles("test")
public class RedisAndRedissonRecommendTest extends TestContainerSupport {
    @Autowired
    private RedisCountService redisCountService;

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
        boardRecommendRepository.deleteAllInBatch();
        boardCountRepository.deleteAllInBatch();
        boardRepository.deleteAllInBatch();
    }

    @DisplayName("여러 회원이 동시에 추천을 눌렀을 때 정합성을 보장한다.")
    @Test
    void multiMemberConcurrentRecommendTest() throws InterruptedException {
        // given
        Board board = createBoard(member, Category.BASEBALL, CategoryType.FREE, "야구 카테고리 제목", "야구 카테고리 내용");

        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);
        StopWatch stopWatch = new StopWatch();

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

        stopWatch.start();

        // when
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
                } catch (Exception e) {
                    System.err.println("❗예외 발생: " + e.getMessage());
                } finally {
                    SecurityContextHolder.clearContext();
                    latch.countDown();
                }
            });
        }

        latch.await();
        stopWatch.stop();

        // then
        CommonCountDto result = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.BOARD, board.getId(),
                null);

        System.out.println("✅ 최종 추천 수: " + result.getRecommendCount());
        System.out.println("⏱ 총 걸린 시간(ms): " + stopWatch.getTotalTimeMillis());

        Assertions.assertThat(result.getRecommendCount()).isEqualTo(threadCount);
    }
}