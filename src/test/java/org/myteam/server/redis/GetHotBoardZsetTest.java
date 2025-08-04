package org.myteam.server.redis;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.util.redis.RedisCountBulkUpdater;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.global.util.redis.service.RedisCountService;
import org.myteam.server.global.util.redis.service.RedisService;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.entity.MemberActivity;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.recommend.RecommendActionType;
import org.myteam.server.recommend.RecommendService;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.support.TestContainerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
public class GetHotBoardZsetTest extends TestContainerSupport {


    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisCountService redisCountService;

    private List<Member> members=new ArrayList<>();
    private List<Board> boardList=new ArrayList<>();


    @BeforeEach
    void setting(){

        for (int i = 0; i < 10; i++) {
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
        for(int i=0;10>i;i++){
            Board board=createBoard(members.get(i), Category.BASEBALL, CategoryType.FREE,"제목",
                    "내용");
            boardList.add(board);
            System.out.printf("boardid:%d",board.getId());
        }
    }
    @Test
    @DisplayName("여러 사용자가 순위를 매기고 정합성이 잘지켜지는지 그리고 순위가 양수인 데이터만 잘가져오는지.")
    void recommendZsetTest() throws ExecutionException, InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);


        for (Member m: members) {
            executorService.execute(() -> {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(new UsernamePasswordAuthenticationToken(
                        new CustomUserDetails(m),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                ));
                SecurityContextHolder.setContext(context);
                try {
                    boardList.stream().forEach(x->{

                        if(x.getId()%2==0) {

                            redisCountService.getCommonCount(ServiceType.RECOMMEND,
                                    DomainType.BOARD, x.getId(), null);
                        }
                        else{
                            redisCountService.getCommonCount(ServiceType.RECOMMEND_CANCEL,
                                    DomainType.BOARD, x.getId(), null);
                        }
                    });
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        List<Long> ids=redisService.getBoardRecommendRankPerDay();
        assertThat(ids.size()).isEqualTo(5);
        ids.stream().forEach(x->{
            assertThat(x%2).isEqualTo(0);
        });
    }

}
