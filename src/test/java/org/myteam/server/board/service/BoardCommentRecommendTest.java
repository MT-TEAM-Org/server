//package org.myteam.server.board.service;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.junit.jupiter.api.Assertions.assertAll;
//
//import java.util.NoSuchElementException;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.myteam.server.IntegrationTestSupport;
//import org.myteam.server.board.domain.Board;
//import org.myteam.server.board.domain.BoardComment;
//import org.myteam.server.board.domain.BoardCommentRecommend;
//import org.myteam.server.board.domain.BoardType;
//import org.myteam.server.board.domain.CategoryType;
//import org.myteam.server.member.domain.MemberRole;
//import org.myteam.server.member.domain.MemberStatus;
//import org.myteam.server.member.domain.MemberType;
//import org.myteam.server.member.entity.Member;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.transaction.annotation.Transactional;
//
//public class BoardCommentRecommendTest extends IntegrationTestSupport {
//
//    @Autowired
//    private BoardCommentRecommendService boardCommentRecommendService;
//
//    @DisplayName("게시판 댓글 추천 클릭시 사용자 좋아요 추가를 테스트한다.")
//    @Test
//    @Transactional
//    void recommendBoardCommentTest() {
//        // given
//        Member member = createMember(1);
//        Board board = createBoard(member, BoardType.BASEBALL, CategoryType.FREE, "야구 카테고리 제목", "야구 카테고리 내용");
//        BoardComment boardComment = createBoardComment(board, member, "댓글 테스트");
//
//        // when
//        boardCommentRecommendService.recommendBoardComment(boardComment.getId());
//
//        // then
//        assertAll(
//                () -> Assertions.assertThat(
//                                boardCommentRepository.findById(boardComment.getId()).get().getRecommendCount())
//                        .isEqualTo(1),
//                () -> Assertions.assertThat(
//                                boardCommentRecommendRepository.findByBoardCommentIdAndMemberPublicId(boardComment.getId(),
//                                                member.getPublicId())
//                                        .get())
//                        .extracting("boardComment.id", "member.publicId")
//                        .contains(boardComment.getId(), member.getPublicId())
//        );
//    }
//
//    @DisplayName("게시판 댓글 추천 취소시 사용자 좋아요 제거를 테스트한다.")
//    @Test
//    @Transactional
//    void cancelRecommendBoardCommentTest() {
//        // given
//        Member member = createMember(1);
//        Board board = createBoard(member, BoardType.BASEBALL, CategoryType.FREE, "야구 카테고리 제목", "야구 카테고리 내용");
//        BoardComment boardComment = createBoardComment(board, member, "댓글 테스트");
//
//        boardCommentRecommendService.addRecommendCount(boardComment.getId());
//        BoardCommentRecommend boardCommentRecommend = createBoardCommentRecommend(boardComment, member);
//
//        // when
//        boardCommentRecommendService.deleteRecommendBoardComment(boardComment.getId());
//
//        // then
//        assertAll(
//                () -> Assertions.assertThat(
//                                boardCommentRepository.findById(boardComment.getId()).get().getRecommendCount())
//                        .isEqualTo(0),
//                () -> assertThatThrownBy(
//                        () -> boardCommentRecommendRepository.findById(
//                                        boardCommentRecommend.getId())
//                                .get())
//                        .isInstanceOf(NoSuchElementException.class)
//                        .hasMessage("No value present")
//        );
//    }
//
//    @DisplayName("게시판 댓글 추천수 증가 동시성 테스트를 한다.")
//    @Test
//    void addRecommendCountTest() throws InterruptedException, ExecutionException {
//        int threadCount = 50;
//
//        ExecutorService executorService = Executors.newFixedThreadPool(25);
//        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
//
//        Member member = Member.builder()
//                .email("test" + 1 + "@test.com")
//                .password("1234")
//                .tel("12345")
//                .nickname("test")
//                .role(MemberRole.USER)
//                .type(MemberType.LOCAL)
//                .status(MemberStatus.ACTIVE)
//                .build();
//
//        Board board = Board.builder()
//                .member(member)
//                .boardType(BoardType.BASEBALL)
//                .categoryType(CategoryType.FREE)
//                .title("title")
//                .content("content")
//                .link("https://www.naver.com")
//                .createdIp("127.0.0.1")
//                .thumbnail("http://localhost:9000/devbucket/inage/1235.png")
//                .build();
//
//        BoardComment boardComment = BoardComment.builder()
//                .board(board)
//                .member(member)
//                .imageUrl("http://localhost:9000/bucket/test.png")
//                .comment("댓글 테스트")
//                .createdIp("127.0.0.1")
//                .recommendCount(0)
//                .build();
//
//        executorService.submit(() -> {
//            memberJpaRepository.save(member);
//            boardRepository.save(board);
//            boardCommentRepository.save(boardComment);
//        }).get();
//
//        for (int i = 0; i < threadCount; i++) {
//            executorService.execute(() -> {
//                try {
//                    boardCommentRecommendService.addRecommendCount(boardComment.getId());
//                } finally {
//                    countDownLatch.countDown();
//                }
//            });
//        }
//
//        countDownLatch.await();
//
//        assertThat(boardCommentRepository.findById(boardComment.getId()).get().getRecommendCount()).isEqualTo(50);
//    }
//
//    @DisplayName("게시판 댓글 추천수 감소 동시성 테스트를 한다.")
//    @Test
//    void minusRecommendCountTest() throws InterruptedException, ExecutionException {
//        int threadCount = 50;
//
//        ExecutorService executorService = Executors.newFixedThreadPool(25);
//        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
//
//        Member member = Member.builder()
//                .email("test" + 1 + "@test.com")
//                .password("1234")
//                .tel("12345")
//                .nickname("test")
//                .role(MemberRole.USER)
//                .type(MemberType.LOCAL)
//                .status(MemberStatus.ACTIVE)
//                .build();
//
//        Board board = Board.builder()
//                .member(member)
//                .boardType(BoardType.BASEBALL)
//                .categoryType(CategoryType.FREE)
//                .title("title")
//                .content("content")
//                .link("https://www.naver.com")
//                .createdIp("127.0.0.1")
//                .thumbnail("http://localhost:9000/devbucket/inage/1235.png")
//                .build();
//
//        BoardComment boardComment = BoardComment.builder()
//                .board(board)
//                .member(member)
//                .imageUrl("http://localhost:9000/bucket/test.png")
//                .comment("댓글 테스트")
//                .createdIp("127.0.0.1")
//                .recommendCount(50)
//                .build();
//
//        executorService.submit(() -> {
//            memberJpaRepository.save(member);
//            boardRepository.save(board);
//            boardCommentRepository.save(boardComment);
//        }).get();
//
//        for (int i = 0; i < threadCount; i++) {
//            executorService.execute(() -> {
//                try {
//                    boardCommentRecommendService.minusRecommendCount(boardComment.getId());
//                } finally {
//                    countDownLatch.countDown();
//                }
//            });
//        }
//
//        countDownLatch.await();
//
//        assertThat(boardCommentRepository.findById(boardComment.getId()).get().getRecommendCount()).isEqualTo(0);
//    }
//}