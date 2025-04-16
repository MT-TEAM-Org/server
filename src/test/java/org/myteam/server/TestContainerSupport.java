package org.myteam.server;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.MySQLContainer;

@ActiveProfiles("container")
public class TestContainerSupport extends TestDriverSupport {

    // MySql
    private static final String MYSQL_IMAGE = "mysql:8.0";
    private static final JdbcDatabaseContainer MYSQL;

    // minio
    private static final String MINIO_IMAGE = "minio/minio:latest";
    private static final String MINIO_BUCKET_NAME = "play-hive";
    private static final int MINIO_PORT = 9000;
    private static final MinIOContainer MINIO;

    // Redis
    private static final String REDIS_IMAGE = "redis:7.0.8-alpine";
    private static final int REDIS_PORT = 6379;
    private static final GenericContainer REDIS;

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

        // Redis 컨테이너 초기화
        REDIS = new GenericContainer(REDIS_IMAGE)
                .withExposedPorts(REDIS_PORT)
                .withCommand("redis-server --requirepass test")
                .withReuse(true);
        REDIS.start();
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
        registry.add("minio.access-key", () -> "testuser");
        registry.add("minio.secret-key", () -> "testpassword");
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

        // Redis 설정
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(REDIS_PORT)
                .toString());
        registry.add("spring.data.redis.password", () -> "test");
    }
}
