package org.myteam.server;

import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.entity.MemberActivity;
import org.myteam.server.member.repository.MemberActivityRepository;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.news.repository.NewsRepository;
import org.myteam.server.news.newsComment.domain.NewsComment;
import org.myteam.server.news.newsComment.repository.NewsCommentRepository;
import org.myteam.server.news.newsCount.domain.NewsCount;
import org.myteam.server.news.newsCount.repository.NewsCountRepository;
import org.myteam.server.news.newsCountMember.domain.NewsCountMember;
import org.myteam.server.news.newsCountMember.repository.NewsCountMemberRepository;
import org.myteam.server.news.newsReply.domain.NewsReply;
import org.myteam.server.news.newsReply.repository.NewsReplyRepository;
import org.myteam.server.upload.config.S3ConfigLocal;
import org.myteam.server.upload.controller.S3Controller;
import org.myteam.server.upload.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.MySQLContainer;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

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
	protected NewsReplyRepository newsReplyRepository;
	@Autowired
	protected MemberJpaRepository memberJpaRepository;
	@Autowired
	protected MemberActivityRepository memberActivityRepository;
	@Autowired
	protected NewsCountMemberRepository newsCountMemberRepository;
	@Autowired
	protected InquiryRepository inquiryRepository;
	@MockBean
	protected SecurityReadService securityReadService;
	@MockBean
	protected S3ConfigLocal s3ConfigLocal;
	@MockBean
	protected S3Presigner s3Presigner;
	@MockBean
	protected S3Controller s3Controller;
	protected S3Service s3Service;
	protected S3Client s3Client;

	@AfterEach
	void tearDown() {
		inquiryRepository.deleteAllInBatch();
		newsReplyRepository.deleteAllInBatch();
		newsCommentRepository.deleteAllInBatch();
		newsCountMemberRepository.deleteAllInBatch();
		newsCountRepository.deleteAllInBatch();
		newsRepository.deleteAllInBatch();
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

		return savedMember;
	}

	protected News createNews(int index, NewsCategory category, int count) {
		News savedNews = newsRepository.save(News.builder()
			.title("기사타이틀" + index)
			.category(category)
			.thumbImg("www.test.com")
			.postDate(LocalDateTime.now())
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

	protected NewsCountMember createNewsCountMember(Member member, News news) {
		return newsCountMemberRepository.save(
			NewsCountMember.builder()
				.member(member)
				.news(news)
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
				.build()
		);
	}
}
