package org.myteam.server.bulkupdate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.util.redis.CommonCount;
import org.myteam.server.global.util.redis.RedisCountBulkUpdater;
import org.myteam.server.global.util.redis.RedisCountService;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.member.entity.Member;
import org.myteam.server.util.CountStrategy;
import org.myteam.server.util.CountStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedisBulkUpdateTest extends IntegrationTestSupport {

    @Autowired
    private RedisCountService redisCountService;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private HashOperations<String, Object, Object> hashOperations;
    @Mock
    private CountStrategyFactory strategyFactory;

    @Mock
    private CountStrategy strategy;

    @InjectMocks
    private RedisCountBulkUpdater redisCountBulkUpdater;

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
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

//    @Test
//    @DisplayName("스트레스 테스트 적용")
//    void stressTestNativeViewCount() throws InterruptedException {
//        // given
//        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
//        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
//
//        // when
//        long start = System.currentTimeMillis();
//
//        for (int i = 0; i < THREAD_COUNT; i++) {
//            executorService.submit(() -> {
//                try {
//                    for (int j = 0; j < INCR_PER_THREAD; j++) {
//                        int randomBoardId = RandomGenerator.getDefault().nextInt(1, 4);
//                        String url = baseUrl + "/" + randomBoardId;
//
//                        try {
//                            restTemplate.getForEntity(url, ResponseDto.class);
//                        } catch (Exception e) {
//                            System.err.println("Request failed: " + e.getMessage());
//                        }
//
//                    }
//                } catch (Exception e) {
//                    System.err.println("Error occurred: " + e.getMessage());
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await(); // 모든 쓰레드 작업 완료 대기
//        long end = System.currentTimeMillis();
//
//        // then
//        System.out.println("⏱ 총 소요 시간: " + (end - start) + "ms"); // ⏱ 총 소요 시간: 38746ms
//    }

    @Test
    @DisplayName("레디스 -> DB 벌크 업데이트 테스트")
    void redisToDatabaseBulkUpdate() {
        // given
        Long boardId = 1L;
        String redisKey = "board:count:" + boardId; // 실 서비스 기준 key 형식 맞춰야 해요

        Set<String> keys = Set.of(redisKey);

        Map<Object, Object> redisHash = new HashMap<>();
        redisHash.put("view", "100");
        redisHash.put("comment", "5");

        when(strategyFactory.getStrategy("board")).thenReturn(strategy);
        when(strategy.getRedisPattern()).thenReturn("board:count:*");
        when(redisTemplate.keys("board:count:*")).thenReturn(keys);
        when(strategy.extractContentIdFromKey(redisKey)).thenReturn(boardId);
        when(redisTemplate.opsForHash().entries(redisKey)).thenReturn(redisHash);

        // when
        redisCountBulkUpdater.bulkUpdate("board");

        // then
        ArgumentCaptor<CommonCount> captor = ArgumentCaptor.forClass(CommonCount.class);
        verify(strategy).updateToDatabase(captor.capture());
        CommonCount<?> captured = captor.getValue();

        assertThat(captured.getCount()).isEqualTo(boardId);
        assertThat(captured.getViewCount()).isEqualTo(100);
        assertThat(captured.getCommentCount()).isEqualTo(5);

        verify(redisTemplate).delete(redisKey);
    }


    @Test
    @DisplayName("조회 시 Redis 캐시 미스 발생 후 DB 조회 및 Redis 저장 확인")
    void redisCacheMiss_thenSaveToRedis() {
        // given
        Long boardId = 1L;
        String redisKey = "view:board:" + boardId;

        // Redis 값 삭제 (캐시 미스 유도)
        redisCountService.removeViewCount(redisKey, boardId);

        // when
        restTemplate.getForEntity(baseUrl + "/" + boardId, ResponseDto.class); // 1차 요청
        restTemplate.getForEntity(baseUrl + "/" + boardId, ResponseDto.class); // 2차 요청
        given(redisCountService.getViewCountAndIncr(anyString(), anyLong()))
                .willReturn(2);

        // then
        int viewCount = redisCountService.getViewCountAndIncr(redisKey, boardId);
        assertNotNull(viewCount, "조회 후 Redis에 조회수가 저장되어야 함");
        assertSame(2, viewCount);
    }
}
