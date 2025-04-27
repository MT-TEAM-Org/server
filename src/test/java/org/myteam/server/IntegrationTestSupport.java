package org.myteam.server;

import static org.mockito.BDDMockito.given;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.myteam.server.board.service.BoardCountService;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.board.service.BoardRecommendReadService;
import org.myteam.server.board.service.BoardService;
import org.myteam.server.board.util.RedisBoardRankingReader;
import org.myteam.server.comment.service.CommentReadService;
import org.myteam.server.comment.service.CommentService;
import org.myteam.server.global.util.redis.RedisCountService;
import org.myteam.server.global.util.redis.RedisService;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.inquiry.service.InquiryService;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPredictionMember.domain.MatchPredictionMember;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.MemberService;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.mypage.service.MyPageReadService;
import org.myteam.server.recommend.RecommendService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

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
    protected InquiryReadService inquiryReadService;
    @MockBean
    protected SecurityReadService securityReadService;
    @Autowired
    protected MemberService memberService;
    @Autowired
    protected MyPageReadService myPageReadService;
    @Autowired
    protected BoardReadService boardReadService;
    @MockBean
    protected MemberReadService memberReadService;
    @Autowired
    protected BoardCountService boardCountService;
    @Autowired
    protected InquiryService inquiryService;
    @Autowired
    protected CommentReadService commentReadService;
    @Autowired
    protected RecommendService recommendService;

    @AfterEach
    void tearDown() {
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

    protected MatchPredictionMember createMatchPredictionMember(Member member, MatchPrediction matchPrediction) {
        return matchPredictionMemberRepository.save(
                MatchPredictionMember.builder()
                        .matchPrediction(matchPrediction)
                        .member(member)
                        .build()
        );
    }
}