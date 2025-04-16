package org.myteam.server.news.newsCountMember;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.TestContainerSupport;
import org.myteam.server.global.domain.Category;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.newsCountMember.domain.NewsCountMember;
import org.myteam.server.news.newsCountMember.service.NewsCountMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class NewsCountMemberServiceTest extends TestContainerSupport {

    @Autowired
    private NewsCountMemberService newsCountMemberService;

    @MockBean
    private SecurityReadService securityReadService;

    @DisplayName("사용자 추천 데이터를 추가한다.")
    @Test
    void saveTest() {
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

        when(securityReadService.getMember()).thenReturn(member);

        newsCountMemberService.save(news.getId());

        assertThat(newsCountMemberRepository.findByNewsIdAndMemberPublicId(news.getId(), member.getPublicId()).get())
                .extracting("news.id", "member.publicId")
                .contains(news.getId(), member.getPublicId());
    }

    @DisplayName("사용자 추천 데이터를 삭제한다.")
    @Test
    void deleteByNewsIdMemberIdTest() {
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

        when(securityReadService.getMember()).thenReturn(member);

        NewsCountMember newsCountMember = createNewsCountMember(member, news);

        newsCountMemberService.deleteByNewsIdMemberId(news.getId());

        assertThatThrownBy(() -> newsCountMemberRepository.findById(newsCountMember.getId()).get())
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No value present");
    }
}
