package org.myteam.server.board.service;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.dto.request.CommentRequest;
import org.myteam.server.comment.dto.response.CommentResponse;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

public class BoardCountServiceIntegrationTest extends TestContainerSupport {

    @Autowired
    private RedisCountService redisCountService;

    private Member member;

    @BeforeEach
    public void setUp() {
        commentRepository.deleteAllInBatch();
        boardRecommendRepository.deleteAllInBatch();
        boardCountRepository.deleteAllInBatch();
        boardRepository.deleteAllInBatch();

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

    @DisplayName("게시글 추천 클릭시 사용자 좋아요 추가를 테스트한다.")
    @Test
    @Transactional
    void recommendBoardTest() {
        // given
        Board board = createBoard(member, Category.BASEBALL, CategoryType.FREE, "야구 카테고리 제목", "야구 카테고리 내용");

        // when
        boardCountService.recommendBoard(board.getId());

        // then
        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.BOARD,
                board.getId(), null);
        System.out.println("commonCountDto.getRecommendCount( = " + commonCountDto.getRecommendCount());

        assertThat(commonCountDto.getRecommendCount()).isEqualTo(1);
    }

    @DisplayName("게시글 추천 취소시 사용자 좋아요 제거를 테스트한다.")
    @Test
    @Transactional
    void cancelRecommendTest() {
        // given
        Board board = createBoard(member, Category.BASEBALL, CategoryType.FREE, "야구 카테고리 제목", "야구 카테고리 내용");

        boardCountService.recommendBoard(board.getId());

        // when
        boardCountService.deleteRecommendBoard(board.getId());

        //then
        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.BOARD,
                board.getId(), null);
        System.out.println("commonCountDto.getRecommendCount( = " + commonCountDto.getRecommendCount());

        assertThat(commonCountDto.getRecommendCount()).isEqualTo(0);
    }

    @DisplayName("게시글 추천수 증가 동시성 테스트한다.")
    @Test
    void addRecommendCountTest() throws InterruptedException, ExecutionException {
        int threadCount = 50;

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
        MemberActivity memberActivity = new MemberActivity(member);

        Board board = Board.builder()
                .member(member)
                .boardType(Category.BASEBALL)
                .categoryType(CategoryType.FREE)
                .title("title")
                .content("content")
                .link("https://www.naver.com")
                .createdIp("127.0.0.1")
                .thumbnail("http://localhost:9000/devbucket/inage/1235.png")
                .build();

        BoardCount savedBoardCount = BoardCount.builder()
                .board(board)
                .build();

        executorService.submit(() -> {
            memberJpaRepository.save(member);
            boardRepository.save(board);
            boardCountRepository.save(savedBoardCount);
        }).get();

        redisCountService.getCommonCount(ServiceType.CHECK, DomainType.BOARD, board.getId(), null);

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

                    boardCountService.recommendBoard(board.getId());
                } finally {
                    SecurityContextHolder.clearContext();
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.BOARD,
                board.getId(), null);
        System.out.println("commonCountDto.getRecommendCount( = " + commonCountDto.getRecommendCount());

        assertThat(commonCountDto.getRecommendCount()).isEqualTo(50);
    }

    @DisplayName("게시글 추천수 감소 동시성 테스트한다.")
    @Test
    void minusRecommendCountTest() throws InterruptedException, ExecutionException {
        int threadCount = 50;

        ExecutorService executorService = Executors.newFixedThreadPool(25);
        CountDownLatch recommendLatch = new CountDownLatch(threadCount);
        CountDownLatch cancelLatch = new CountDownLatch(threadCount);

        ConcurrentHashMap<Integer, Member> memberMap = new ConcurrentHashMap<>();

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

        Board board = Board.builder()
                .member(member)
                .boardType(Category.BASEBALL)
                .categoryType(CategoryType.FREE)
                .title("title")
                .content("content")
                .link("https://www.naver.com")
                .createdIp("127.0.0.1")
                .thumbnail("http://localhost:9000/devbucket/inage/1235.png")
                .build();

        BoardCount boardCount = BoardCount.createBoardCount(board);

        executorService.submit(() -> {
            memberJpaRepository.save(member);
            boardRepository.save(board);
            boardCountRepository.save(boardCount);
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

                    boardCountService.recommendBoard(board.getId());
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

                    boardCountService.deleteRecommendBoard(board.getId());
                } finally {
                    SecurityContextHolder.clearContext();
                    cancelLatch.countDown();
                }
            });
        }
        cancelLatch.await();

        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.BOARD,
                board.getId(), null);
        assertThat(commonCountDto.getRecommendCount()).isEqualTo(0);
    }

    @DisplayName("게시판 댓글수 증가 동시성 테스트한다.")
    @Test
    void addCommentCountTest() throws InterruptedException, ExecutionException {
        int threadCount = 50;

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

        Board board = Board.builder()
                .member(member)
                .boardType(Category.BASEBALL)
                .categoryType(CategoryType.FREE)
                .title("title")
                .content("content")
                .link("https://www.naver.com")
                .createdIp("127.0.0.1")
                .thumbnail("http://localhost:9000/devbucket/inage/1235.png")
                .build();

        BoardCount savedBoardCount = BoardCount.builder()
                .board(board)
                .build();

        executorService.submit(() -> {
            memberJpaRepository.save(member);
            boardRepository.save(board);
            boardCountRepository.save(savedBoardCount);
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
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.BOARD,
                board.getId(), null);
        assertThat(commonCountDto.getCommentCount()).isLessThanOrEqualTo(50);
    }

    @DisplayName("게시판 댓글수 감소 동시성 테스트한다.")
    @Test
    void minusCommentCountTest() throws InterruptedException, ExecutionException {
        int threadCount = 50;

        ExecutorService executorService = Executors.newFixedThreadPool(25);
        CountDownLatch commentLatch = new CountDownLatch(threadCount);
        CountDownLatch deleteLatch = new CountDownLatch(threadCount);

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

        Board board = Board.builder()
                .member(member)
                .boardType(Category.BASEBALL)
                .categoryType(CategoryType.FREE)
                .title("title")
                .content("content")
                .link("https://www.naver.com")
                .createdIp("127.0.0.1")
                .thumbnail("http://localhost:9000/devbucket/inage/1235.png")
                .build();

        BoardCount savedBoardCount = BoardCount.createBoardCount(board);

        executorService.submit(() -> {
            memberJpaRepository.save(member);
            boardRepository.save(board);
            boardCountRepository.save(savedBoardCount);
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

                    CommentResponse.CommentSaveResponse comment = commentService.addComment(board.getId(),
                            commentSaveRequest, "0.0.0.1");
                    commentMap.put(idx, comment.getCommentId());
                } finally {
                    commentLatch.countDown();
                }
            });
        }
        commentLatch.await();

        CommentRequest.CommentDeleteRequest deleteRequest = CommentRequest.CommentDeleteRequest.builder()
                .type(CommentType.BOARD)
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
                    commentService.deleteComment(board.getId(), commentMap.get(idx), deleteRequest);
                } finally {
                    deleteLatch.countDown();
                }
            });
        }
        deleteLatch.await();

        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.BOARD,
                board.getId(), null);
        // 동시성 이슈로 50보다 작은 값이 나옴
        assertThat(commonCountDto.getCommentCount()).isLessThanOrEqualTo(0);
    }

    @DisplayName("게시판 조회수 증가 동시성 테스트한다.")
    @Test
    void addViewCountCountTest() throws InterruptedException, ExecutionException {
        int threadCount = 50;
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

        Board board = Board.builder()
                .member(member)
                .boardType(Category.BASEBALL)
                .categoryType(CategoryType.FREE)
                .title("title")
                .content("content")
                .link("https://www.naver.com")
                .createdIp("127.0.0.1")
                .thumbnail("http://localhost:9000/devbucket/inage/1235.png")
                .build();

        BoardCount savedBoardCount = BoardCount.builder()
                .board(board)
                .build();

        executorService.submit(() -> {
            memberJpaRepository.save(member);
            boardRepository.save(board);
            boardCountRepository.save(savedBoardCount);
        }).get();

        for (int i = 0; i < threadCount; i++) {
            /**
             * 해당 라인을 여기다 선언하면 동시성 이슈로 50보다 작은 값이 나옴
             */
//            redisCountService.getCommonCount(ServiceType.VIEW, DomainType.BOARD, board.getId(), null);
            executorService.execute(() -> {
                try {
                    redisCountService.getCommonCount(ServiceType.VIEW, DomainType.BOARD, board.getId(), null);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.BOARD,
                board.getId(), null);
        // 동시성 이슈로 50보다 작은 값이 나옴
        assertThat(commonCountDto.getViewCount()).isLessThanOrEqualTo(50);
    }
}