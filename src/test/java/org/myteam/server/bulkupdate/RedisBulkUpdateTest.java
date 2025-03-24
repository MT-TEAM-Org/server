package org.myteam.server.bulkupdate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.ControllerTestSupport;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.dto.reponse.BoardResponse;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.util.redis.RedisService;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedisBulkUpdateTest extends IntegrationTestSupport {

    @Autowired
    private RedisService redisService;

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
    @DisplayName("DB 직접 조회수 증가 성능 테스트 (레디스 미적용)")
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

//    @Test
//    @DisplayName("레디스를 통한 조회수 증가 성능 테스트")
//    void stressTestRedisViewCount() throws InterruptedException {
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
//                        // TODO: 레디스에 넣기.
//                        // redisService
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
//        // TODO: 확인
//        String finalCount = redisTemplate.opsForValue().get(targetKey);
//
//        System.out.println("🔥 최종 조회수: " + finalCount);
//        System.out.println("⏱ 총 소요 시간: " + (end - start) + "ms");
//
//        executorService.shutdown();
//
//        // then
//        assertEquals(String.valueOf(THREAD_COUNT * INCR_PER_THREAD), finalCount);
//    }
//
//    @Test
//    @DisplayName("레디스 -> DB 벌크 업데이트 테스트")
//    void redisToDatabaseBulkUpdate() throws InterruptedException {
//        // given
//        Long boardId = 1L;
//        String redisKey = "view:board:" + boardId;
//
//        // 1. Redis에 조회수 저장
//        for (int i = 0; i < 100; i++) {
//            redisService.increment(redisKey); // ex) RedisService의 incrBy
//        }
//
//        // 2. Bulk 업데이트 메서드 실행 (직접 메서드 호출)
//        bulkUpdateService.syncViewCountsToDatabase();
//
//        // 3. DB에서 조회수 확인
//        int dbViewCount = boardCountRepository.findByBoardId(boardId)
//                .map(BoardCount::getViewCount)
//                .orElse(0);
//
//        // 4. Redis가 초기화되었는지도 확인 (선택)
//        String redisViewCount = redisService.get(redisKey);
//
//        // then
//        assertEquals(100, dbViewCount, "DB에 저장된 조회수는 100이어야 함");
//        assertTrue(redisViewCount == null || redisViewCount.equals("0"), "Bulk 이후 Redis 값은 초기화되어야 함");
//    }
//
//    @Test
//    @DisplayName("조회 시 Redis 캐시 미스 발생 후 DB 조회 및 Redis 저장 확인")
//    void redisCacheMiss_thenSaveToRedis() {
//        Long boardId = 1L;
//        String redisKey = "view:board:" + boardId;
//
//        // Redis 값 삭제 (미스 유도)
//        // TODO
//        redisService.delete(redisKey);
//
//        // 1차 요청 - 캐시 미스 → DB 조회 → Redis 저장
//        restTemplate.getForEntity(baseUrl + "/" + boardId, ResponseDto.class);
//
//        // 2차 요청 - 캐시 히트
//        restTemplate.getForEntity(baseUrl + "/" + boardId, ResponseDto.class);
//
//        // TODO
//        String viewCount = redisService.get(redisKey);
//        System.out.println("📌 Redis에 저장된 조회수: " + viewCount);
//
//        assertNotNull(viewCount);
//    }
}
