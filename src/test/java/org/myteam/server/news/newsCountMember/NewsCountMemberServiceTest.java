package org.myteam.server.news.newsCountMember;

import static org.assertj.core.api.Assertions.*;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.newsCountMember.domain.NewsCountMember;
import org.myteam.server.news.newsCountMember.service.NewsCountMemberService;
import org.springframework.beans.factory.annotation.Autowired;

public class NewsCountMemberServiceTest extends IntegrationTestSupport {

	@Autowired
	private NewsCountMemberService newsCountMemberService;

	@DisplayName("사용자 추천 데이터를 추가한다.")
	@Test
	void saveTest() {
		News news = createNews(1, NewsCategory.FOOTBALL, 10);
		Member member = createMember(1);

		newsCountMemberService.save(news.getId());

		assertThat(newsCountMemberRepository.findByNewsIdAndMemberPublicId(news.getId(), member.getPublicId()).get())
			.extracting("news.id", "member.publicId")
			.contains(news.getId(), member.getPublicId());
	}

	@DisplayName("사용자 추천 데이터를 삭제한다.")
	@Test
	void deleteByNewsIdMemberIdTest() {
		News news = createNews(1, NewsCategory.FOOTBALL, 10);
		Member member = createMember(1);

		NewsCountMember newsCountMember = createNewsCountMember(member, news);

		newsCountMemberService.deleteByNewsIdMemberId(news.getId());

		assertThatThrownBy(() -> newsCountMemberRepository.findById(newsCountMember.getId()).get())
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage("No value present");
	}
}
