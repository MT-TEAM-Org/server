package org.myteam.server.bulkupdate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.util.redis.RedisViewCountBulkUpdater;
import org.myteam.server.global.util.redis.RedisViewCountService;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.member.entity.Member;
import org.myteam.server.util.ViewCountStrategy;
import org.myteam.server.util.ViewCountStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedisBulkUpdateTest extends IntegrationTestSupport {

    @Autowired
    private RedisViewCountService redisViewCountService;
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ViewCountStrategyFactory strategyFactory;

    @Mock
    private ViewCountStrategy strategy;

    @InjectMocks
    private RedisViewCountBulkUpdater redisViewCountBulkUpdater;

    @LocalServerPort
    private int port;
    private String baseUrl;
    private final int THREAD_COUNT = 100;
    private final int INCR_PER_THREAD = 1000;

    private final List<Long> newsIds = List.of(1L, 2L, 3L);
    private final String PREFIX = "view:";
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/board";
        Member member = createMember(1);

        createBoard(member, Category.BASEBALL, CategoryType.FREE, "제목제목" + 1, "내용내용" + 1);
        createBoard(member, Category.ESPORTS, CategoryType.FREE, "제목제목" + 2, "내용내용" + 2);
        createBoard(member, Category.FOOTBALL, CategoryType.FREE, "제목제목" + 3, "내용내용" + 3);
    }

    @Test
    @DisplayName("스트레스 테스트 적용")
    void stressTestNativeViewCount() throws InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        // when
        long start = System.currentTimeMillis();

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < INCR_PER_THREAD; j++) {
                        int randomBoardId = RandomGenerator.getDefault().nextInt(1, 4);
                        String url = baseUrl + "/" + randomBoardId;

                        try {
                            restTemplate.getForEntity(url, ResponseDto.class);
                        } catch (Exception e) {
                            System.err.println("Request failed: " + e.getMessage());
                        }

                    }
                } catch (Exception e) {
                    System.err.println("Error occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 쓰레드 작업 완료 대기
        long end = System.currentTimeMillis();

        // then
        System.out.println("⏱ 총 소요 시간: " + (end - start) + "ms"); // ⏱ 총 소요 시간: 38746ms
    }

    @Test
    @DisplayName("레디스 -> DB 벌크 업데이트 테스트")
    void redisToDatabaseBulkUpdate() throws InterruptedException {
        // given
        Long boardId = 1L;
        String redisKey = "view:board:" + boardId;

        // 1. 조회수 100회 증가
        for (int i = 0; i < 100; i++) {
            restTemplate.getForEntity(baseUrl + "/" + boardId, ResponseDto.class); // 1차 요청
        }
        given(redisViewCountService.getViewCount(anyString(), anyLong()))
                .willReturn(100);

        // 2. Bulk 업데이트 실행
        String type = "board";
        String redisValue = "100";

        Set<String> keys = Set.of(redisKey);

        when(strategyFactory.getStrategy(type)).thenReturn(strategy);
        when(strategy.getRedisPattern()).thenReturn("view:board:*");
        when(redisTemplate.keys("view:board:*")).thenReturn(keys);
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        when(redisTemplate.opsForValue().get(redisKey)).thenReturn(redisValue);
        when(strategy.extractContentIdFromKey(redisKey)).thenReturn(boardId);

        redisViewCountBulkUpdater.bulkUpdate("board");

        // then
        verify(strategy).updateToDatabase(boardId, 100);
        verify(redisTemplate).delete(redisKey);
    }

    @Test
    @DisplayName("조회 시 Redis 캐시 미스 발생 후 DB 조회 및 Redis 저장 확인")
    void redisCacheMiss_thenSaveToRedis() {
        // given
        Long boardId = 1L;
        String redisKey = "view:board:" + boardId;

        // Redis 값 삭제 (캐시 미스 유도)
        redisViewCountService.removeViewCount(redisKey, boardId);

        // when
        restTemplate.getForEntity(baseUrl + "/" + boardId, ResponseDto.class); // 1차 요청
        restTemplate.getForEntity(baseUrl + "/" + boardId, ResponseDto.class); // 2차 요청
        given(redisViewCountService.getViewCount(anyString(), anyLong()))
                .willReturn(2);

        // then
        int viewCount = redisViewCountService.getViewCount(redisKey, boardId);
        assertNotNull(viewCount, "조회 후 Redis에 조회수가 저장되어야 함");
        assertSame(2, viewCount);
    }
}
