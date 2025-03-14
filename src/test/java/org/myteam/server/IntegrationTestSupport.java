package org.myteam.server;

import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardComment;
import org.myteam.server.board.domain.BoardCommentRecommend;
import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.board.domain.BoardRecommend;
import org.myteam.server.board.domain.BoardReply;
import org.myteam.server.board.domain.BoardReplyRecommend;
import org.myteam.server.board.domain.BoardType;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.repository.BoardCommentRecommendRepository;
import org.myteam.server.board.repository.BoardCommentRepository;
import org.myteam.server.board.repository.BoardCountRepository;
import org.myteam.server.board.repository.BoardRecommendRepository;
import org.myteam.server.board.repository.BoardReplyRecommendRepository;
import org.myteam.server.board.repository.BoardReplyRepository;
import org.myteam.server.board.repository.BoardRepository;
import org.myteam.server.board.service.BoardCountReadService;
import org.myteam.server.board.service.BoardCountService;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.board.service.BoardRecommendReadService;
import org.myteam.server.board.service.BoardReplyRecommendService;
import org.myteam.server.board.service.BoardService;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.inquiry.service.InquiryService;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.match.repository.MatchRepository;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPrediction.repository.MatchPredictionRepository;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.myteam.server.match.team.repository.TeamRepository;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberActivityRepository;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.MemberService;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.mypage.service.MyPageReadService;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.news.repository.NewsRepository;
import org.myteam.server.news.newsComment.domain.NewsComment;
import org.myteam.server.news.newsComment.repository.NewsCommentRepository;
import org.myteam.server.news.newsCommentMember.domain.NewsCommentMember;
import org.myteam.server.news.newsCommentMember.repository.NewsCommentMemberRepository;
import org.myteam.server.news.newsCount.domain.NewsCount;
import org.myteam.server.news.newsCount.repository.NewsCountRepository;
import org.myteam.server.news.newsCountMember.domain.NewsCountMember;
import org.myteam.server.news.newsCountMember.repository.NewsCountMemberRepository;
import org.myteam.server.news.newsReply.domain.NewsReply;
import org.myteam.server.news.newsReply.repository.NewsReplyRepository;
import org.myteam.server.news.newsReplyMember.domain.NewsReplyMember;
import org.myteam.server.news.newsReplyMember.repository.NewsReplyMemberRepository;
import org.myteam.server.upload.config.S3ConfigAws;
import org.myteam.server.upload.config.S3ConfigLocal;
import org.myteam.server.upload.controller.S3Controller;
import org.myteam.server.upload.service.AwsS3Service;
import org.myteam.server.upload.service.S3Service;
import org.myteam.server.util.slack.service.SlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

    /**
     * ================== Repository ========================
     */
    @Autowired
    protected TeamRepository teamRepository;
    @Autowired
    protected MatchRepository matchRepository;
    @Autowired
    protected NewsRepository newsRepository;
    @Autowired
    protected NewsCountRepository newsCountRepository;
    @Autowired
    protected NewsCommentRepository newsCommentRepository;
    @Autowired
    protected NewsCommentMemberRepository newsCommentMemberRepository;
    @Autowired
    protected NewsReplyRepository newsReplyRepository;
    @Autowired
    protected NewsReplyMemberRepository newsReplyMemberRepository;
    @Autowired
    protected MemberJpaRepository memberJpaRepository;
    @Autowired
    protected MemberActivityRepository memberActivityRepository;
    @Autowired
    protected NewsCountMemberRepository newsCountMemberRepository;
    @Autowired
    protected InquiryService inquiryService;
    @Autowired
    protected InquiryRepository inquiryRepository;
    @Autowired
    protected MatchPredictionRepository matchPredictionRepository;
    @Autowired
    protected BoardRepository boardRepository;
    @Autowired
    protected BoardCountRepository boardCountRepository;
    @Autowired
    protected BoardRecommendRepository boardRecommendRepository;
    @Autowired
    protected BoardCommentRepository boardCommentRepository;
    @Autowired
    protected BoardCommentRecommendRepository boardCommentRecommendRepository;
    @Autowired
    protected BoardReplyRepository boardReplyRepository;
    @Autowired
    protected BoardReplyRecommendRepository boardReplyRecommendRepository;

    /**
     * ================== Service ========================
     */
    @MockBean
    protected InquiryReadService inquiryReadService;
    @MockBean
    protected SecurityReadService securityReadService;
    @Autowired
    protected MemberService memberService;
    @Autowired
    protected MyPageReadService myPageReadService;
    @MockBean
    protected BoardService boardService;
    @Autowired
    protected BoardReadService boardReadService;
    @MockBean
    protected MemberReadService memberReadService;
    @Autowired
    protected BoardCountReadService boardCountReadService;
    @Autowired
    protected BoardRecommendReadService boardRecommendReadService;
    @Autowired
    protected BoardReplyRecommendService boardReplyRecommendService;
    @Autowired
    protected BoardCountService boardCountService;

    /**
     * ================== Config ========================
     */
    @MockBean
    protected S3ConfigLocal s3ConfigLocal;
    @MockBean
    protected S3ConfigAws s3ConfigAws;
    @MockBean
    @Qualifier("s3Presigner")
    protected S3Presigner s3Presigner;
    @MockBean
    protected S3Controller s3Controller;
    @MockBean
    protected S3Service s3Service;
    @MockBean
    protected AwsS3Service awsS3Service;
    @MockBean
    protected SlackService slackService;

    @AfterEach
    void tearDown() {
        matchPredictionRepository.deleteAllInBatch();
        matchRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        inquiryRepository.deleteAllInBatch();
        newsReplyMemberRepository.deleteAllInBatch();
        newsReplyRepository.deleteAllInBatch();
        newsCommentMemberRepository.deleteAllInBatch();
        newsCommentRepository.deleteAllInBatch();
        newsCountMemberRepository.deleteAllInBatch();
        newsCountRepository.deleteAllInBatch();
        newsRepository.deleteAllInBatch();
        boardReplyRecommendRepository.deleteAllInBatch();
        boardReplyRepository.deleteAllInBatch();
        boardCommentRecommendRepository.deleteAllInBatch();
        boardCommentRepository.deleteAllInBatch();
        boardRecommendRepository.deleteAllInBatch();
        boardCountRepository.deleteAllInBatch();
        boardRepository.deleteAllInBatch();
        memberActivityRepository.deleteAllInBatch();
        memberJpaRepository.deleteAllInBatch();
    }

    protected Member createMember(int index) {
        Member member = Member.builder()
                .email("test" + index + "@test.com")
                .password("1234")
                .tel("12345")
                .nickname("test")
                .role(MemberRole.USER)
                .type(MemberType.LOCAL)
                .publicId(UUID.randomUUID())
                .status(MemberStatus.ACTIVE)
                .build();

        Member savedMember = memberJpaRepository.save(member);

        given(securityReadService.getMember())
                .willReturn(savedMember);

        given(securityReadService.getAuthenticatedPublicId())
                .willReturn(member.getPublicId());

        return savedMember;
    }

    protected News createNews(int index, NewsCategory category, int count) {
        News savedNews = newsRepository.save(News.builder()
                .title("기사타이틀" + index)
                .category(category)
                .thumbImg("www.test.com")
                .postDate(LocalDateTime.now())
                .source("www.test.com")
                .content("뉴스본문")
                .build());

        NewsCount newsCount = NewsCount.builder()
                .recommendCount(count)
                .commentCount(count)
                .viewCount(count)
                .build();

        newsCount.updateNews(savedNews);

        newsCountRepository.save(newsCount);

        return savedNews;
    }

    protected News createNewsWithPostDate(int index, NewsCategory category, int count, LocalDateTime postTime) {
        News savedNews = newsRepository.save(News.builder()
                .title("기사타이틀" + index)
                .category(category)
                .thumbImg("www.test.com")
                .postDate(postTime)
                .source("www.test.com")
                .content("뉴스본문")
                .build());

        NewsCount newsCount = NewsCount.builder()
                .recommendCount(count)
                .commentCount(count)
                .viewCount(count)
                .build();

        newsCount.updateNews(savedNews);

        newsCountRepository.save(newsCount);

        return savedNews;
    }

    protected NewsCountMember createNewsCountMember(Member member, News news) {
        return newsCountMemberRepository.save(
                NewsCountMember.builder()
                        .news(news)
                        .member(member)
                        .build()
        );
    }

    protected NewsComment createNewsComment(News news, Member member, String comment, int recommendCount) {
        return newsCommentRepository.save(
                NewsComment.builder()
                        .news(news)
                        .member(member)
                        .comment(comment)
                        .ip("1.1.1.1")
                        .imgUrl("www.test.com")
                        .recommendCount(recommendCount)
                        .build()
        );
    }

    protected NewsReply createNewsReply(NewsComment newsComment, Member member, String comment) {
        return newsReplyRepository.save(
                NewsReply.builder()
                        .newsComment(newsComment)
                        .member(member)
                        .comment(comment)
                        .ip("1.1.1.1")
                        .imgUrl("www.test.com")
                        .build()
        );
    }

    protected NewsCommentMember createNewsCommentMember(Member member, NewsComment newsComment) {
        return newsCommentMemberRepository.save(
                NewsCommentMember.builder()
                        .member(member)
                        .newsComment(newsComment)
                        .build()
        );
    }

    protected NewsReplyMember createNewsReplyMember(Member member, NewsReply newsReply) {
        return newsReplyMemberRepository.save(
                NewsReplyMember.builder()
                        .member(member)
                        .newsReply(newsReply)
                        .build()
        );
    }

    protected Team createTeam(int index, TeamCategory category) {
        return teamRepository.save(Team.builder()
                .name("테스트팀" + index)
                .logo("www.test.com")
                .category(category)
                .build());
    }

    protected Match createMatch(Team homeTeam, Team awayTeam, MatchCategory category,
                                LocalDateTime startDate) {
        return matchRepository.save(Match.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .category(category)
                .startTime(startDate)
                .build());
    }

    protected MatchPrediction createMatchPrediction(Match match, int home, int away) {
        return matchPredictionRepository.save(MatchPrediction.builder()
                .match(match)
                .home(home)
                .away(away)
                .build());
    }

    protected Board createBoard(Member member, BoardType boardType, CategoryType categoryType, String title,
                                String content) {

        Board board = Board.builder()
                .member(member)
                .boardType(boardType)
                .categoryType(categoryType)
                .title(title)
                .content(content)
                .link("https://www.naver.com")
                .createdIp("127.0.0.1")
                .thumbnail("http://localhost:9000/devbucket/inage/1235.png")
                .build();

        boardRepository.save(board);

        BoardCount boardCount = BoardCount.builder()
                .board(board)
                .recommendCount(0)
                .commentCount(0)
                .viewCount(0)
                .build();

        boardCountRepository.save(boardCount);

        return board;
    }

    protected BoardRecommend createBoardRecommend(Board board, Member member) {
        BoardRecommend recommend = BoardRecommend.builder()
                .board(board)
                .member(member)
                .build();
        boardRecommendRepository.save(recommend);

        return recommend;
    }

    protected BoardComment createBoardComment(Board board, Member member, String comment) {
        BoardComment boardComment = BoardComment.builder()
                .board(board)
                .member(member)
                .imageUrl("http://localhost:9000/bucket/test.png")
                .comment(comment)
                .createdIp("127.0.0.1")
                .recommendCount(0)
                .build();

        boardCommentRepository.save(boardComment);

        return boardComment;
    }

    protected BoardCommentRecommend createBoardCommentRecommend(BoardComment boardComment, Member member) {
        BoardCommentRecommend boardCommentRecommend = BoardCommentRecommend.builder()
                .boardComment(boardComment)
                .member(member)
                .build();
        boardCommentRecommendRepository.save(boardCommentRecommend);

        return boardCommentRecommend;
    }

    protected BoardReply createBoardReply(BoardComment boardComment, Member member, String comment,
                                          Member mentionedMember) {
        BoardReply boardReply = BoardReply.builder()
                .boardComment(boardComment)
                .member(member)
                .imageUrl("http://localhost:9000/bucket/test.png")
                .comment(comment)
                .createdIp("127.0.0.1")
                .recommendCount(0)
                .mentionedMember(mentionedMember)
                .build();

        boardReplyRepository.save(boardReply);

        return boardReply;
    }

    protected BoardReplyRecommend createBoardReplyRecommend(BoardReply boardReply, Member member) {
        BoardReplyRecommend boardReplyRecommend = BoardReplyRecommend.builder()
                .boardReply(boardReply)
                .member(member)
                .build();
        boardReplyRecommendRepository.save(boardReplyRecommend);

        return boardReplyRecommend;
    }
}
