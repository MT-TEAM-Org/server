package org.myteam.server.support;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.myteam.server.admin.entity.AdminContentChangeLog;
import org.myteam.server.admin.entity.AdminImproveChangeLog;
import org.myteam.server.admin.entity.AdminMemberMemo;
import org.myteam.server.admin.repository.simpleRepo.*;
import org.myteam.server.board.service.BoardCountService;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.board.util.RedisBoardRankingReader;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.comment.service.CommentReadService;
import org.myteam.server.common.certification.mail.core.MailStrategy;
import org.myteam.server.common.certification.mail.domain.EmailType;
import org.myteam.server.global.util.redis.service.RedisCountService;
import org.myteam.server.global.util.redis.service.RedisService;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.inquiry.service.InquiryService;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPrediction.dto.service.request.Side;
import org.myteam.server.match.matchPredictionMember.domain.MatchPredictionMember;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.entity.MemberAccess;
import org.myteam.server.member.entity.MemberActivity;
import org.myteam.server.member.repository.MemberAccessRepository;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.MemberService;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.mypage.service.MyPageReadService;
import org.myteam.server.recommend.RecommendService;
import org.myteam.server.report.domain.Report;
import org.myteam.server.report.domain.ReportType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = {
        RedisAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class
})
public abstract class IntegrationTestSupport extends TestDriverSupport {

    /**
     * =================== Config ===================
     */
    @MockBean
    protected RedissonClient redissonClient;
    @MockBean
    protected RedisTemplate<String, Object> redisTemplate;
    @MockBean
    protected RedisCountService redisCountService;
    @MockBean
    protected RedisBoardRankingReader redisBoardRankingReader;
    @MockBean
    protected RedisService redisService;

    /**
     * =================== Service ===================
     */
    @MockBean
    protected SecurityReadService securityReadService;
    @Autowired
    protected MemberService memberService;
    @Autowired
    protected MyPageReadService myPageReadService;
    @Autowired
    protected BoardReadService boardReadService;
    @Autowired
    protected BoardCountService boardCountService;
    @Autowired
    protected InquiryService inquiryService;
    @Autowired
    protected InquiryReadService inquiryReadService;
    @Autowired
    protected CommentReadService commentReadService;
    @Autowired
    protected RecommendService recommendService;
    @Autowired
    protected MemberAccessRepository memberAccessRepository;

    @Autowired
    protected AdminContentMemoRepo adminContentMemoRepo;
    @Autowired
    protected AdminContentChangeLogRepo adminContentChangeLogRepo;
    @Autowired
    protected AdminMemberChangeLogRepo adminMemberChangeLogRepo;
    @Autowired
    protected AdminImproveChangeLogRepo adminImproveChangeLogRepo;
    @Autowired
    protected AdminInquiryChangeLogRepo adminInquiryChangeLogRepo;
    @Autowired
    protected AdminMemberMemoRepo adminMemberMemoRepo;
    @AfterEach
    void tearDown() {
        adminMemberMemoRepo.deleteAllInBatch();;
        adminInquiryChangeLogRepo.deleteAllInBatch();
        adminImproveChangeLogRepo.deleteAllInBatch();
        adminMemberChangeLogRepo.deleteAllInBatch();;
        adminContentChangeLogRepo.deleteAllInBatch();
        adminContentMemoRepo.deleteAllInBatch();
        commentRecommendRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        matchPredictionMemberRepository.deleteAllInBatch();
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
        improvementRecommendRepository.deleteAllInBatch();
        improvementCountRepository.deleteAllInBatch();
        improvementRepository.deleteAllInBatch();
        noticeRecommendRepository.deleteAllInBatch();
        noticeCountRepository.deleteAllInBatch();
        noticeRepository.deleteAllInBatch();
        reportRepository.deleteAllInBatch();
        memberActivityRepository.deleteAllInBatch();
        memberAccessRepository.deleteAllInBatch();
        memberJpaRepository.deleteAllInBatch();
    }

    @Transactional
    protected Member createMemberByService(int index) {
        MailStrategy mockStrategy = mock(MailStrategy.class);
        when(mailStrategyFactory.getStrategy(EmailType.WELCOME)).thenReturn(mockStrategy);
        doReturn(CompletableFuture.completedFuture(null))
                .when(mockStrategy).send(anyString());

        memberService.create(
                MemberSaveRequest.builder()
                        .email("test" + index + "@test.com")
                        .nickname("nickname")
                        .tel("01001011010")
                        .password("1234")
                        .build()
        );

        return memberJpaRepository.findByEmail("test" + index + "@test.com").get();
    }

    @Transactional
    protected Member createMember(int index) {
        Member member = Member.builder()
                .email("test" + index + "@test.com")
                .password("1234")
                .tel("01012345678")
                .nickname("test" + index)
                .role(MemberRole.USER)
                .type(MemberType.LOCAL)
                .publicId(UUID.randomUUID())
                .status(MemberStatus.ACTIVE)
                .build();

        MemberActivity memberActivity = new MemberActivity(member);
        Member savedMember = memberJpaRepository.save(member);

        given(securityReadService.getMember())
                .willReturn(savedMember);

        given(securityReadService.getAuthenticatedPublicId())
                .willReturn(member.getPublicId());

        return savedMember;
    }

    @Transactional
    protected Member createMemberWithOutSave(int index) {
        Member member = Member.builder()
                .email("test" + index + "@test.com")
                .password("1234")
                .tel("01012345678")
                .nickname("test" + index)
                .role(MemberRole.USER)
                .type(MemberType.LOCAL)
                .publicId(UUID.randomUUID())
                .status(MemberStatus.ACTIVE)
                .build();
        memberJpaRepository.save(member);
        return member;
    }


    protected Member createOAuthMember(int index) {
        Member member = Member.builder()
                .email("test" + index + "@test.com")
                .password("1234")
                .tel(null)
                .nickname(null)
                .role(MemberRole.USER)
                .type(MemberType.KAKAO)
                .publicId(UUID.randomUUID())
                .status(MemberStatus.PENDING)
                .build();

        MemberActivity memberActivity = new MemberActivity(member);
        Member savedMember = memberJpaRepository.save(member);

        given(securityReadService.getMember())
                .willReturn(savedMember);

        given(securityReadService.getAuthenticatedPublicId())
                .willReturn(member.getPublicId());

        return savedMember;
    }

    protected Member createOAuthMemberNonPending(int index) {
        Member member = Member.builder()
                .email("test" + index + "@test.com")
                .password("1234")
                .tel(null)
                .nickname(null)
                .role(MemberRole.USER)
                .type(MemberType.KAKAO)
                .publicId(UUID.randomUUID())
                .status(MemberStatus.ACTIVE)
                .build();

        MemberActivity memberActivity = new MemberActivity(member);
        Member savedMember = memberJpaRepository.save(member);

        given(securityReadService.getMember())
                .willReturn(savedMember);

        given(securityReadService.getAuthenticatedPublicId())
                .willReturn(member.getPublicId());

        return savedMember;
    }

    protected Member createNonAuthMember(int index) {
        Member member = Member.builder()
                .email("test" + index + "@test.com")
                .password("1234")
                .tel("01012345678")
                .nickname("test")
                .role(MemberRole.USER)
                .type(MemberType.LOCAL)
                .publicId(UUID.randomUUID())
                .status(MemberStatus.INACTIVE)
                .build();

        MemberActivity memberActivity = new MemberActivity(member);
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

        MemberActivity memberActivity = new MemberActivity(admin);
        Member savedMember = memberJpaRepository.save(admin);

        given(securityReadService.getMember())
                .willReturn(savedMember);

        given(securityReadService.getAuthenticatedPublicId())
                .willReturn(admin.getPublicId());

        return savedMember;
    }

    protected MatchPredictionMember createMatchPredictionMember(Member member, MatchPrediction matchPrediction) {
        return matchPredictionMemberRepository.save(
                MatchPredictionMember.builder()
                        .matchPrediction(matchPrediction)
                        .member(member)
                        .side(Side.HOME)
                        .build()
        );
    }

    protected Report createReport(Member reporter, Member reported, BanReason reason,
                                  ReportType type, Long reportedContentId) {
        return reportRepository.save(
                Report.builder()
                        .reporter(reporter)
                        .reported(reported)
                        .reason(reason)
                        .reportIp("127.0.0.1")
                        .reportType(type)
                        .reportedContentId(reportedContentId)
                        .build()
        );
    }

    protected MemberAccess createMemberAccess(Member member, LocalDateTime now){
        System.out.println("createAccess");

        MemberAccess memberAccess=MemberAccess
                .builder()
                .publicId(member.getPublicId())
                .accessTime(now)
                .build();
        memberAccessRepository.save(memberAccess);

        return memberAccess;


    }
}