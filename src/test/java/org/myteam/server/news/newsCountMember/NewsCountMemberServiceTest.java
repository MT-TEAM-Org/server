package org.myteam.server.news.newsCountMember;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.TestContainerSupport;
import org.myteam.server.global.domain.Category;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.entity.MemberActivity;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.newsCountMember.domain.NewsCountMember;
import org.myteam.server.news.newsCountMember.dto.service.response.NewsCountMemberDeleteResponse;
import org.myteam.server.news.newsCountMember.dto.service.response.NewsCountMemberResponse;
import org.myteam.server.news.newsCountMember.service.NewsCountMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

public class NewsCountMemberServiceTest extends TestContainerSupport {

    @Autowired
    private NewsCountMemberService newsCountMemberService;

    @MockBean
    private SecurityReadService securityReadService;

    private Member member;
    private News news;

    @BeforeEach
    void setUp() {
        member = createMember(1);
        news = createNews(1, Category.FOOTBALL, 1);
    }

    @DisplayName("사용자 추천 데이터를 추가한다.")
    @Test
    void saveTest() {
        News news = createNews(1, Category.FOOTBALL, 10);
        Member member = createMember();

        newsCountMemberService.save(news.getId());

        assertThat(newsCountMemberRepository.findByNewsIdAndMemberPublicId(news.getId(), member.getPublicId()).get())
                .extracting("news.id", "member.publicId")
                .contains(news.getId(), member.getPublicId());
    }

    @DisplayName("사용자 추천 데이터를 삭제한다.")
    @Test
    void deleteByNewsIdMemberIdTest() {
        News news = createNews(1, Category.FOOTBALL, 10);
        Member member = createMember();

        NewsCountMember newsCountMember = createNewsCountMember(member, news);

        newsCountMemberService.deleteByNewsIdMemberId(news.getId());

        assertThatThrownBy(() -> newsCountMemberRepository.findById(newsCountMember.getId()).get())
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No value present");
    }

    @Test
    @DisplayName("뉴스 추천 기록 저장 성공")
    void save_success() {
        // given
        when(securityReadService.getMember()).thenReturn(member);

        // when
        NewsCountMemberResponse response = newsCountMemberService.save(news.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(newsCountMemberRepository.findByNewsIdAndMemberPublicId(news.getId(), member.getPublicId()))
                .isPresent();
    }

    @Test
    @DisplayName("뉴스 추천 기록 삭제 성공")
    void deleteByNewsIdMemberId_success() {
        // given
        when(securityReadService.getMember()).thenReturn(member);
        newsCountMemberRepository.save(
                NewsCountMember.builder()
                        .news(news)
                        .member(member)
                        .build()
        );

        // when
        NewsCountMemberDeleteResponse response = newsCountMemberService.deleteByNewsIdMemberId(news.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(newsCountMemberRepository.findByNewsIdAndMemberPublicId(news.getId(), member.getPublicId()))
                .isNotPresent();
    }

    // --- 테스트용 헬퍼 메서드 ---
    private Member createMember(int index) {
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
        return memberJpaRepository.save(member);
    }

    @Transactional
    protected Member createMember() {
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

        MemberActivity memberActivity = new MemberActivity(member);
        Member savedMember = memberJpaRepository.save(member);

        given(securityReadService.getMember())
                .willReturn(savedMember);

        given(securityReadService.getAuthenticatedPublicId())
                .willReturn(member.getPublicId());

        return savedMember;
    }
}
