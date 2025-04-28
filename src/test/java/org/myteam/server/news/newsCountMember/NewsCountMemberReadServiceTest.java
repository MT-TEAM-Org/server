package org.myteam.server.news.newsCountMember;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.TestContainerSupport;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.entity.MemberActivity;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.newsCountMember.domain.NewsCountMember;
import org.myteam.server.news.newsCountMember.service.NewsCountMemberReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

public class NewsCountMemberReadServiceTest extends TestContainerSupport {

    @Autowired
    private NewsCountMemberReadService newsCountMemberReadService;
    @MockBean
    private SecurityReadService securityReadService;

    private Member member;
    private News news;

    @BeforeEach
    void setUp() {
        member = createMember(1);
        news = createNews(1, Category.FOOTBALL, 1);
    }

    @DisplayName("이미 좋아요를 누른 뉴스면 예외가 발생한다.")
    @Test
    void confirmExistMemberTest() {
        News news = createNews(1, Category.FOOTBALL, 10);

        Member member = Member.builder()
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

        createNewsCountMember(member, news);

        assertThatThrownBy(() -> newsCountMemberReadService.confirmExistMember(news.getId(), member.getPublicId()))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.ALREADY_MEMBER_RECOMMEND_NEWS.getMsg());

    }

    @Test
    @DisplayName("confirmExistMember - 이미 추천했으면 예외 발생")
    void confirmExistMember_alreadyRecommended_throwsException() {
        // given
        createNewsCountMember(news, member);

        // when & then
        assertThatThrownBy(() -> newsCountMemberReadService.confirmExistMember(news.getId(), member.getPublicId()))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.ALREADY_MEMBER_RECOMMEND_NEWS.getMsg());
    }

    @Test
    @DisplayName("confirmExistMember - 추천하지 않았으면 정상 통과")
    void confirmExistMember_notRecommended_success() {
        // when & then
        assertThatCode(() -> newsCountMemberReadService.confirmExistMember(news.getId(), member.getPublicId()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("confirmRecommendMember - 추천했으면 true 반환")
    void confirmRecommendMember_recommended_true() {
        // given
        createNewsCountMember(news, member);

        // when
        boolean result = newsCountMemberReadService.confirmRecommendMember(news.getId(), member.getPublicId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("confirmRecommendMember - 추천하지 않았으면 false 반환")
    void confirmRecommendMember_notRecommended_false() {
        // when
        boolean result = newsCountMemberReadService.confirmRecommendMember(news.getId(), member.getPublicId());

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isRecommended - 추천했으면 true 반환")
    void isRecommended_recommended_true() {
        // given
        createNewsCountMember(news, member);

        // when
        boolean result = newsCountMemberReadService.isRecommended(news.getId(), member.getPublicId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isRecommended - 추천하지 않았으면 false 반환")
    void isRecommended_notRecommended_false() {
        // when
        boolean result = newsCountMemberReadService.isRecommended(news.getId(), member.getPublicId());

        // then
        assertThat(result).isFalse();
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
    protected void createNewsCountMember(News news, Member member) {
        newsCountMemberRepository.save(
                NewsCountMember.builder()
                        .news(news)
                        .member(member)
                        .build()
        );
    }
}
