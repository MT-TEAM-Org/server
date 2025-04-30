# Step 1: 애플리케이션 빌드
FROM openjdk:17-jdk-slim AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper와 관련 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
RUN chmod +x ./gradlew

# Gradle 종속성을 캐싱하여 빌드 속도 향상
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드 (bootJar 실행)
RUN ./gradlew clean bootJar --no-daemon

# Step 2: 런타임 이미지 생성
FROM openjdk:17-jdk-slim

# 실행 디렉토리 설정
WORKDIR /play-hive

# 빌드 단계에서 생성된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션이 사용할 포트 노출
EXPOSE 8080

# 애플리케이션 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
