package org.myteam.server.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.board.domain.BoardRecommend;
import org.myteam.server.board.domain.BoardType;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.springframework.transaction.annotation.Transactional;

public class BoardCountServiceTest extends IntegrationTestSupport {

    @DisplayName("게시글 추천 클릭시 사용자 좋아요 추가를 테스트한다.")
    @Test
    @Transactional
    void recommendBoardTest() {
        // given
        Member member = createMember(1);
        Board board = createBoard(member, BoardType.BASEBALL, CategoryType.FREE, "야구 카테고리 제목", "야구 카테고리 내용");
        // when
        boardCountService.recommendBoard(board.getId());

        // then
        assertAll(
                () -> Assertions.assertThat(boardCountRepository.findByBoardId(board.getId()).get().getRecommendCount())
                        .isEqualTo(1),
                () -> Assertions.assertThat(
                                boardRecommendRepository.findByBoardIdAndMemberPublicId(board.getId(), member.getPublicId())
                                        .get())
                        .extracting("board.id", "member.publicId")
                        .contains(board.getId(), member.getPublicId())
        );
    }

    @DisplayName("게시글 추천 취소시 사용자 좋아요 제거를 테스트한다.")
    @Test
    @Transactional
    void cancelRecommendTest() {
        // given
        Member member = createMember(1);
        Board board = createBoard(member, BoardType.BASEBALL, CategoryType.FREE, "야구 카테고리 제목", "야구 카테고리 내용");

        boardCountService.addRecommendCount(board.getId());
        BoardRecommend boardRecommend = createBoardRecommend(board, member);

        // when
        boardCountService.deleteRecommendBoard(board.getId());

        //then
        assertAll(
                () -> assertThat(boardCountRepository.findByBoardId(board.getId()).get().getRecommendCount()).isEqualTo(
                        0),
                () -> assertThatThrownBy(() -> boardRecommendRepository.findById(boardRecommend.getId()).get())
                        .isInstanceOf(NoSuchElementException.class)
                        .hasMessage("No value present")
        );
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
                .encodedPassword("1234")
                .tel("12345")
                .nickname("test")
                .role(MemberRole.USER)
                .type(MemberType.LOCAL)
                .status(MemberStatus.ACTIVE)
                .build();

        Board board = Board.builder()
                .member(member)
                .boardType(BoardType.BASEBALL)
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
            executorService.execute(() -> {
                try {
                    boardCountService.addRecommendCount(board.getId());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        assertThat(boardCountRepository.findById(savedBoardCount.getId()).get().getRecommendCount()).isEqualTo(50);
    }

    @DisplayName("게시글 추천수 감소 동시성 테스트한다.")
    @Test
    void minusRecommendCountTest() throws InterruptedException, ExecutionException {
        int threadCount = 50;

        ExecutorService executorService = Executors.newFixedThreadPool(25);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        Member member = Member.builder()
                .email("test" + 1 + "@test.com")
                .password("1234")
                .encodedPassword("1234")
                .tel("12345")
                .nickname("test")
                .role(MemberRole.USER)
                .type(MemberType.LOCAL)
                .status(MemberStatus.ACTIVE)
                .build();

        Board board = Board.builder()
                .member(member)
                .boardType(BoardType.BASEBALL)
                .categoryType(CategoryType.FREE)
                .title("title")
                .content("content")
                .link("https://www.naver.com")
                .createdIp("127.0.0.1")
                .thumbnail("http://localhost:9000/devbucket/inage/1235.png")
                .build();

        BoardCount savedBoardCount = BoardCount.builder()
                .board(board)
                .recommendCount(50)
                .build();

        executorService.submit(() -> {
            memberJpaRepository.save(member);
            boardRepository.save(board);
            boardCountRepository.save(savedBoardCount);
        }).get();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    boardCountService.minusRecommendCount(board.getId());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        assertThat(boardCountRepository.findById(savedBoardCount.getId()).get().getRecommendCount()).isEqualTo(0);
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
                .encodedPassword("1234")
                .tel("12345")
                .nickname("test")
                .role(MemberRole.USER)
                .type(MemberType.LOCAL)
                .status(MemberStatus.ACTIVE)
                .build();

        Board board = Board.builder()
                .member(member)
                .boardType(BoardType.BASEBALL)
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
            executorService.execute(() -> {
                try {
                    boardCountService.addCommentCount(board.getId());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        assertThat(boardCountRepository.findById(savedBoardCount.getId()).get().getCommentCount())
                .isEqualTo(50);
    }

    @DisplayName("게시판 댓글수 감소 동시성 테스트한다.")
    @Test
    void minusCommentCountTest() throws InterruptedException, ExecutionException {
        int threadCount = 50;

        ExecutorService executorService = Executors.newFixedThreadPool(25);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        Member member = Member.builder()
                .email("test" + 1 + "@test.com")
                .password("1234")
                .encodedPassword("1234")
                .tel("12345")
                .nickname("test")
                .role(MemberRole.USER)
                .type(MemberType.LOCAL)
                .status(MemberStatus.ACTIVE)
                .build();

        Board board = Board.builder()
                .member(member)
                .boardType(BoardType.BASEBALL)
                .categoryType(CategoryType.FREE)
                .title("title")
                .content("content")
                .link("https://www.naver.com")
                .createdIp("127.0.0.1")
                .thumbnail("http://localhost:9000/devbucket/inage/1235.png")
                .build();

        BoardCount savedBoardCount = BoardCount.builder()
                .board(board)
                .commentCount(50)
                .build();

        executorService.submit(() -> {
            memberJpaRepository.save(member);
            boardRepository.save(board);
            boardCountRepository.save(savedBoardCount);
        }).get();

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    boardCountService.minusCommentCount(board.getId());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        assertThat(boardCountRepository.findById(savedBoardCount.getId()).get().getCommentCount())
                .isEqualTo(0);
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
                .encodedPassword("1234")
                .tel("12345")
                .nickname("test")
                .role(MemberRole.USER)
                .type(MemberType.LOCAL)
                .status(MemberStatus.ACTIVE)
                .build();

        Board board = Board.builder()
                .member(member)
                .boardType(BoardType.BASEBALL)
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
            executorService.execute(() -> {
                try {
                    boardCountService.addViewCount(board.getId());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        assertThat(boardCountRepository.findById(savedBoardCount.getId()).get().getViewCount()).isEqualTo(50);
    }
}