package org.myteam.server;

import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.board.domain.BoardRecommend;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.repository.BoardCountRepository;
import org.myteam.server.board.repository.BoardRecommendRepository;
import org.myteam.server.board.repository.BoardRepository;
import org.myteam.server.board.service.BoardCountReadService;
import org.myteam.server.board.service.BoardCountService;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.board.service.BoardRecommendReadService;
import org.myteam.server.board.service.BoardService;
import org.myteam.server.comment.domain.NewsComment;
import org.myteam.server.comment.repository.CommentRecommendRepository;
import org.myteam.server.comment.repository.CommentRepository;
import org.myteam.server.comment.service.CommentReadService;
import org.myteam.server.comment.service.CommentService;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.util.redis.RedisCountService;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementCount;
import org.myteam.server.improvement.repository.ImprovementCountRepository;
import org.myteam.server.improvement.repository.ImprovementRepository;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryCount;
import org.myteam.server.inquiry.repository.InquiryCountRepository;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.inquiry.service.InquiryService;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.match.repository.MatchRepository;
import org.myteam.server.match.match.service.MatchReadService;
import org.myteam.server.match.match.service.MatchYoutubeService;
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
import org.myteam.server.news.news.repository.NewsRepository;
import org.myteam.server.news.newsCount.domain.NewsCount;
import org.myteam.server.news.newsCount.repository.NewsCountRepository;
import org.myteam.server.news.newsCountMember.domain.NewsCountMember;
import org.myteam.server.news.newsCountMember.repository.NewsCountMemberRepository;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.domain.NoticeCount;
import org.myteam.server.notice.repository.NoticeCountRepository;
import org.myteam.server.notice.repository.NoticeRepository;
import org.myteam.server.recommend.RecommendService;
import org.myteam.server.upload.config.S3ConfigLocal;
import org.myteam.server.upload.controller.S3Controller;
import org.myteam.server.upload.service.StorageService;
import org.myteam.server.util.slack.service.SlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@ActiveProfiles("test")
public abstract class IntegrationTestSupport extends TestDriverSupport{

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
    protected BoardCountService boardCountService;
    @Autowired
    protected InquiryService inquiryService;
    @MockBean
    protected CommentService commentService;
    @Autowired
    protected CommentReadService commentReadService;
    @Autowired
    protected RecommendService recommendService;
    @MockBean
    protected MatchYoutubeService matchYoutubeService;
    @MockBean
    protected MatchReadService matchReadService;

    @AfterEach
    void tearDown() {
        commentRecommendRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        matchPredictionRepository.deleteAllInBatch();
        matchRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
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

    protected Member createAdmin(int index) {
        Member admin = Member.builder()
                .email("test" + index + "@test.com")
                .password("1234")
                .tel("12345")
                .nickname("test")
                .role(MemberRole.ADMIN)
                .type(MemberType.LOCAL)
                .publicId(UUID.randomUUID())
                .status(MemberStatus.ACTIVE)
                .build();

        Member savedMember = memberJpaRepository.save(admin);

        given(securityReadService.getMember())
                .willReturn(savedMember);

        given(securityReadService.getAuthenticatedPublicId())
                .willReturn(admin.getPublicId());

        return savedMember;
    }
}
