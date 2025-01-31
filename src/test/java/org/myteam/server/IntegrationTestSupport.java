package org.myteam.server;

import static org.mockito.BDDMockito.*;

import java.util.UUID;

import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.domain.News;
import org.myteam.server.news.domain.NewsCategory;
import org.myteam.server.news.domain.NewsComment;
import org.myteam.server.news.domain.NewsCount;
import org.myteam.server.news.repository.NewsCommentRepository;
import org.myteam.server.news.repository.NewsCountRepository;
import org.myteam.server.news.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

	@Autowired
	protected NewsRepository newsRepository;
	@Autowired
	protected NewsCountRepository newsCountRepository;
	@Autowired
	protected NewsCommentRepository newsCommentRepository;
	@Autowired
	protected MemberJpaRepository memberJpaRepository;
	@MockBean
	protected SecurityReadService securityReadService;

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

		return savedMember;
	}

	protected News createNews(int index, NewsCategory category, int count) {
		News savedNews = newsRepository.save(News.builder()
			.title("기사타이틀" + index)
			.category(category)
			.thumbImg("www.test.com")
			.build());

		NewsCount newsCount = NewsCount.builder()
			.news(savedNews)
			.likeCount(count)
			.commentCount(count)
			.viewCount(count)
			.build();

		newsCountRepository.save(newsCount);

		return savedNews;
	}

	protected NewsComment craeteNewsComment(News news, Member member, String comment) {
		return newsCommentRepository.save(
			NewsComment.builder()
				.news(news)
				.member(member)
				.comment(comment)
				.ip("1.1.1.1")
				.build()
		);
	}
}
