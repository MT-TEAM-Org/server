package org.myteam.server;

import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.myteam.server.aop.CommonCountAspect;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.board.domain.BoardRecommend;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.repository.BoardCountRepository;
import org.myteam.server.board.repository.BoardRecommendRepository;
import org.myteam.server.board.repository.BoardRepository;
import org.myteam.server.board.service.BoardCountService;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.comment.domain.NewsComment;
import org.myteam.server.comment.repository.CommentRecommendRepository;
import org.myteam.server.comment.repository.CommentRepository;
import org.myteam.server.comment.service.CommentService;
import org.myteam.server.comment.util.CommentFactory;
import org.myteam.server.common.certification.mail.factory.MailStrategyFactory;
import org.myteam.server.global.config.RedisConfig;
import org.myteam.server.global.domain.Category;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementCount;
import org.myteam.server.improvement.repository.ImprovementCountRepository;
import org.myteam.server.improvement.repository.ImprovementRecommendRepository;
import org.myteam.server.improvement.repository.ImprovementRepository;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryCount;
import org.myteam.server.inquiry.repository.InquiryCountRepository;
import org.myteam.server.inquiry.repository.InquiryQueryRepository;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.match.repository.MatchRepository;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPrediction.repository.MatchPredictionRepository;
import org.myteam.server.match.matchPredictionMember.repository.MatchPredictionMemberRepository;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.myteam.server.match.team.repository.TeamRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberActivityRepository;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.repository.NewsRepository;
import org.myteam.server.news.newsCount.domain.NewsCount;
import org.myteam.server.news.newsCount.repository.NewsCountRepository;
import org.myteam.server.news.newsCount.service.NewsCountService;
import org.myteam.server.news.newsCountMember.domain.NewsCountMember;
import org.myteam.server.news.newsCountMember.repository.NewsCountMemberRepository;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.domain.NoticeCount;
import org.myteam.server.notice.repository.NoticeCountRepository;
import org.myteam.server.notice.repository.NoticeRecommendRepository;
import org.myteam.server.notice.repository.NoticeRepository;
import org.myteam.server.notice.service.NoticeRecommendReadService;
import org.myteam.server.report.repository.ReportRepository;
import org.myteam.server.upload.config.S3ConfigLocal;
import org.myteam.server.upload.controller.S3Controller;
import org.myteam.server.upload.service.StorageService;
import org.myteam.server.util.slack.service.SlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@SpringBootTest
public abstract class TestDriverSupport {

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
    protected MemberJpaRepository memberJpaRepository;
    @Autowired
    protected MemberActivityRepository memberActivityRepository;
    @Autowired
    protected NewsCountMemberRepository newsCountMemberRepository;
    @Autowired
    protected InquiryRepository inquiryRepository;
    @Autowired
    protected InquiryCountRepository inquiryCountRepository;
    @Autowired
    protected InquiryQueryRepository inquiryQueryRepository;
    @Autowired
    protected MatchPredictionRepository matchPredictionRepository;
    @Autowired
    protected BoardRepository boardRepository;
    @Autowired
    protected BoardCountRepository boardCountRepository;
    @Autowired
    protected BoardRecommendRepository boardRecommendRepository;
    @Autowired
    protected NoticeRepository noticeRepository;
    @Autowired
    protected NoticeCountRepository noticeCountRepository;
    @Autowired
    protected NoticeRecommendRepository noticeRecommendRepository;
    @Autowired
    protected MatchPredictionMemberRepository matchPredictionMemberRepository;
    @Autowired
    protected ImprovementRepository improvementRepository;
    @Autowired
    protected ImprovementCountRepository improvementCountRepository;
    @Autowired
    protected ImprovementRecommendRepository improvementRecommendRepository;
    @Autowired
    protected CommentRepository commentRepository;
    @Autowired
    protected CommentRecommendRepository commentRecommendRepository;
    @Autowired
    protected ReportRepository reportRepository;

    /**
     * ================== Service ========================
     */
    @Autowired
    protected BoardCountService boardCountService;
    @Autowired
    protected CommentService commentService;
    @Autowired
    protected NewsCountService newsCountService;

    /**
     * ================== Config ========================
     */
    @MockBean
    protected S3ConfigLocal s3ConfigLocal;
    @MockBean
    protected S3Presigner s3Presigner;
    @MockBean
    protected S3Controller s3Controller;
    @MockBean
    protected StorageService s3Service;
    @MockBean
    protected SlackService slackService;
    @Autowired
    protected RedisConfig redisConfig;
    @Autowired
    protected CommentFactory commentFactory;
    @MockBean
    protected MailStrategyFactory mailStrategyFactory;
    @Autowired
    protected CommonCountAspect commonCountAspect;

    @AfterEach
    void tearDown() {
        commentRecommendRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        matchPredictionMemberRepository.deleteAllInBatch();
        matchPredictionRepository.deleteAllInBatch();
        matchRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        inquiryCountRepository.deleteAllInBatch();
        inquiryRepository.deleteAllInBatch();
        newsCountMemberRepository.deleteAllInBatch();
        newsCountRepository.deleteAllInBatch();
        newsRepository.deleteAllInBatch();
        boardRecommendRepository.deleteAllInBatch();
        boardCountRepository.deleteAllInBatch();
        boardRepository.deleteAllInBatch();
        memberActivityRepository.deleteAllInBatch();
        memberJpaRepository.deleteAllInBatch();
    }

    protected News createNews(int index, Category category, int count) {
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

    protected Comment createNewsComment(News news, Member member, String comment) {
        NewsComment savedNewsComment = commentRepository.save(
                NewsComment.builder()
                        .news(news)
                        .comment(comment)
                        .imageUrl("www.test.com")
                        .member(member)
                        .build()
        );
        return savedNewsComment;
    }

    protected News createNewsWithPostDate(int index, Category category, int count, LocalDateTime postTime) {
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
                .endTime(startDate.plusHours(1))
                .build());
    }

    protected MatchPrediction createMatchPrediction(Match match, int home, int away) {
        return matchPredictionRepository.save(MatchPrediction.builder()
                .match(match)
                .home(home)
                .away(away)
                .build());
    }

    protected Board createBoard(Member member, Category boardType, CategoryType categoryType, String title,
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

    protected Notice createNotice(Member member, String title, String content, String imgUrl) {
        Notice notice = Notice.builder()
                .member(member)
                .title(title)
                .content(content)
                .createdIp("0.0.0.1")
                .imgUrl(imgUrl)
                .build();

        noticeRepository.save(notice);

        NoticeCount noticeCount = NoticeCount.builder()
                .notice(notice)
                .recommendCount(0)
                .commentCount(0)
                .viewCount(0)
                .build();

        noticeCountRepository.save(noticeCount);

        return notice;
    }

    protected Inquiry createInquiry(Member member) {
        Inquiry inquiry = Inquiry.builder()
                .content("문의사항ㅇㅇㅇ")
                .member(member)
                .clientIp("0.0.0.1")
                .createdAt(LocalDateTime.now())
                .isAdminAnswered(false)
                .build();

        inquiryRepository.save(inquiry);

        InquiryCount inquiryCount = InquiryCount.createCount(inquiry);
        inquiryCountRepository.save(inquiryCount);

        return inquiry;
    }

    protected Improvement createImprovement(Member member, boolean isImage) {
        Improvement improvement = Improvement.builder()
                .member(member)
                .title("개선요처어엉ㅇ 제목")
                .content("개선개선개선")
                .createdIp("0.0.0.1")
                .imgUrl(isImage ? "test.co.kr" : null)
                .build();
        improvementRepository.save(improvement);

        ImprovementCount improvementCount = ImprovementCount.createImprovementCount(improvement);
        improvementCountRepository.save(improvementCount);

        return improvement;
    }
}
