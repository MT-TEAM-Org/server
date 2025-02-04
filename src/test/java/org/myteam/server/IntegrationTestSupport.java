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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.MySQLContainer;

//@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

	// MySql
	private static final String MYSQL_IMAGE = "mysql:8.0";
	private static final JdbcDatabaseContainer MYSQL;

	// minio
	private static final String MINIO_IMAGE = "minio/minio:latest";
	private static final String MINIO_BUCKET_NAME = "play-hive";
	private static final int MINIO_PORT = 9000;
	private static final MinIOContainer MINIO;

	static {
		// MySQL 컨테이너 초기화
		MYSQL = new MySQLContainer<>(MYSQL_IMAGE)
				.withDatabaseName("testdb")
				.withUsername("testuser")
				.withPassword("testpassword");
//                .withInitScript("init.sql");
		MYSQL.start();

		// MinIO 컨테이너 초기화
		MINIO = new MinIOContainer(MINIO_IMAGE)
				.withUserName("testuser")
				.withPassword("testpassword")
				.withEnv("MINIO_ACCESS_KEY", "testuser")
				.withEnv("MINIO_SECRET_KEY", "testpassword")
				.withCommand("server /data")
				.withExposedPorts(MINIO_PORT);
		MINIO.start();
	}

	@DynamicPropertySource
	public static void overrideProps(DynamicPropertyRegistry registry) {

		// 💡 (application-dev.yml에 맞춰 설정)
		// ✅ MySQL 설정
		registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
		registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
		registry.add("spring.datasource.username", MYSQL::getUsername);
		registry.add("spring.datasource.password", MYSQL::getPassword);

		// ✅ JPA 설정 (기본적인 값 유지)
		registry.add("spring.jpa.show-sql", () -> "false");
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
		registry.add("spring.jpa.open-in-view", () -> "true");

		// ✅ MinIO 설정 (환경변수에 맞춰 적용)
		registry.add("minio.url", MINIO::getS3URL);
		registry.add("minio.port", () -> MINIO_PORT);
		registry.add("minio.root-user", MINIO::getUserName);
		registry.add("minio.root-password", MINIO::getPassword);
		registry.add("minio.region", () -> "north-east-2");
		registry.add("minio.bucket", () -> MINIO_BUCKET_NAME);

		// ✅ Slack Webhook 설정
		registry.add("slack.webhook.url", () -> "https://mock-slack-webhook-url");

		// ✅ 테스트 환경에서 SENDER_EMAIL & SENDER_PASSWORD 추가
		registry.add("SENDER_EMAIL", () -> "test-email@example.com"); // 💡 기본 테스트 이메일
		registry.add("SENDER_PASSWORD", () -> "test-password"); // 💡 기본 테스트 패스워드

		// 💡 (application-mail.yml 적용)
		// ✅ 메일 설정
		registry.add("spring.mail.host", () -> "smtp.naver.com");
		registry.add("spring.mail.port", () -> "587");
		registry.add("spring.mail.username", () -> "test-email@example.com");
		registry.add("spring.mail.password", () -> "test-password");
		registry.add("spring.mail.properties.mail.smtp.auth", () -> "true");
		registry.add("spring.mail.properties.mail.smtp.timeout", () -> "5000");
		registry.add("spring.mail.properties.mail.smtp.starttls.enable", () -> "true");
	}

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
