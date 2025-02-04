package org.myteam.server.news.newsCount.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.news.repository.NewsRepository;
import org.myteam.server.news.newsCount.domain.NewsCount;
import org.myteam.server.news.newsCount.repository.NewsCountRepository;
import org.myteam.server.news.newsCountMember.domain.NewsCountMember;
import org.springframework.beans.factory.annotation.Autowired;

public class NewsCountServiceTest extends IntegrationTestSupport {

	@Autowired
	private NewsCountService newsCountService;
	@Autowired
	private NewsRepository newsRepository;
	@Autowired
	private NewsCountRepository newsCountRepository;

	@DisplayName("뉴스 추천 클릭시 사용자 좋아요 추가를 테스트한다.")
	@Test
	void recommendNewsTest() {
		News news = createNews(1, NewsCategory.FOOTBALL, 10);
		Member member = createMember(1);

		// when
		newsCountService.recommendNews(news.getId());

		// then
		assertAll(
			() -> assertThat(newsCountRepository.findById(news.getId()).get().getRecommendCount()).isEqualTo(11),
			() -> assertThat(newsCountMemberRepository.findByNewsIdAndMemberId(news.getId(), member.getId()).get())
				.extracting("news.id", "member.id")
				.contains(news.getId(), member.getId())
		);
	}

	@DisplayName("뉴스 추천 취소시 사용자 좋아요 제거를 테스트한다.")
	@Test
	void cancelRecommendNewsTest() {
		News news = createNews(1, NewsCategory.FOOTBALL, 10);
		Member member = createMember(1);
		NewsCountMember newsCountMember = createNewsCountMember(member, news);

		// when
		newsCountService.cancelRecommendNews(news.getId());

		// then
		assertAll(
			() -> assertThat(newsCountRepository.findById(news.getId()).get().getRecommendCount()).isEqualTo(9),
			() -> assertThatThrownBy(() -> newsCountMemberRepository.findById(newsCountMember.getId()).get())
				.isInstanceOf(NoSuchElementException.class)
				.hasMessage("No value present")
		);
	}

	@DisplayName("뉴스 추천수 증가 동시성 테스트한다.")
	@Test
	void addRecommendCountTest() throws InterruptedException, ExecutionException {
		int threadCount = 50;

		ExecutorService executorService = Executors.newFixedThreadPool(25);

		CountDownLatch countDownLatch = new CountDownLatch(threadCount);

		News news = News.builder()
			.title("기사타이틀" + 1)
			.category(NewsCategory.FOOTBALL)
			.thumbImg("www.test.com")
			.build();

		NewsCount savedNewsCount = NewsCount.builder()
			.news(news)
			.build();

		executorService.submit(() -> {
			newsRepository.save(news);
			newsCountRepository.save(savedNewsCount);
		}).get();

		// when
		for (int i = 0; i < threadCount; i++) {
			executorService.execute(() -> {
				try {
					newsCountService.addRecommendCount(news.getId());
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();

		// then
		assertThat(newsCountRepository.findById(savedNewsCount.getId()).get().getRecommendCount())
			.isEqualTo(50);
	}

	@DisplayName("뉴스 추천수 감소 동시성 테스트한다.")
	@Test
	void minusRecommendCountTest() throws InterruptedException, ExecutionException {
		int threadCount = 50;

		ExecutorService executorService = Executors.newFixedThreadPool(25);

		CountDownLatch countDownLatch = new CountDownLatch(threadCount);

		News news = News.builder()
			.title("기사타이틀" + 1)
			.category(NewsCategory.FOOTBALL)
			.thumbImg("www.test.com")
			.build();

		NewsCount savedNewsCount = NewsCount.builder()
			.news(news)
			.recommendCount(50)
			.build();

		executorService.submit(() -> {
			newsRepository.save(news);
			newsCountRepository.save(savedNewsCount);
		}).get();

		// when
		for (int i = 0; i < threadCount; i++) {
			executorService.execute(() -> {
				try {
					newsCountService.minusRecommendCount(news.getId());
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();

		// then
		assertThat(newsCountRepository.findById(savedNewsCount.getId()).get().getRecommendCount())
			.isEqualTo(0);
	}

	@DisplayName("뉴스 댓글수 증가 동시성 테스트한다.")
	@Test
	void addCommentCountTest() throws InterruptedException, ExecutionException {
		int threadCount = 50;

		ExecutorService executorService = Executors.newFixedThreadPool(25);

		CountDownLatch countDownLatch = new CountDownLatch(threadCount);

		News news = News.builder()
			.title("기사타이틀" + 1)
			.category(NewsCategory.FOOTBALL)
			.thumbImg("www.test.com")
			.build();

		NewsCount savedNewsCount = NewsCount.builder()
			.news(news)
			.build();

		executorService.submit(() -> {
			newsRepository.save(news);
			newsCountRepository.save(savedNewsCount);
		}).get();

		// when
		for (int i = 0; i < threadCount; i++) {
			executorService.execute(() -> {
				try {
					newsCountService.addCommendCount(news.getId());
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();

		// then
		assertThat(newsCountRepository.findById(savedNewsCount.getId()).get().getCommentCount())
			.isEqualTo(50);
	}

	@DisplayName("뉴스 댓글수 감소 동시성 테스트한다.")
	@Test
	void minusCommentCountTest() throws InterruptedException, ExecutionException {
		int threadCount = 50;

		ExecutorService executorService = Executors.newFixedThreadPool(25);

		CountDownLatch countDownLatch = new CountDownLatch(threadCount);

		News news = News.builder()
			.title("기사타이틀" + 1)
			.category(NewsCategory.FOOTBALL)
			.thumbImg("www.test.com")
			.build();

		NewsCount savedNewsCount = NewsCount.builder()
			.news(news)
			.commentCount(50)
			.build();

		executorService.submit(() -> {
			newsRepository.save(news);
			newsCountRepository.save(savedNewsCount);
		}).get();

		// when
		for (int i = 0; i < threadCount; i++) {
			executorService.execute(() -> {
				try {
					newsCountService.minusCommendCount(news.getId());
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();

		// then
		assertThat(newsCountRepository.findById(savedNewsCount.getId()).get().getRecommendCount())
			.isEqualTo(0);
	}

	@DisplayName("뉴스 조회수 증가 동시성 테스트한다.")
	@Test
	void addViewCountTest() throws InterruptedException, ExecutionException {
		int threadCount = 50;

		ExecutorService executorService = Executors.newFixedThreadPool(25);

		CountDownLatch countDownLatch = new CountDownLatch(threadCount);

		News news = News.builder()
			.title("기사타이틀" + 1)
			.category(NewsCategory.FOOTBALL)
			.thumbImg("www.test.com")
			.build();

		NewsCount savedNewsCount = NewsCount.builder()
			.news(news)
			.build();

		executorService.submit(() -> {
			newsRepository.save(news);
			newsCountRepository.save(savedNewsCount);
		}).get();

		// when
		for (int i = 0; i < threadCount; i++) {
			executorService.execute(() -> {
				try {
					newsCountService.addViewCount(news.getId());
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();

		// then
		assertThat(newsCountRepository.findById(savedNewsCount.getId()).get().getViewCount())
			.isEqualTo(50);
	}

	@DisplayName("뉴스 조회수 감소 동시성 테스트한다.")
	@Test
	void minusViewCountTest() throws InterruptedException, ExecutionException {
		int threadCount = 50;

		ExecutorService executorService = Executors.newFixedThreadPool(25);

		CountDownLatch countDownLatch = new CountDownLatch(threadCount);

		News news = News.builder()
			.title("기사타이틀" + 1)
			.category(NewsCategory.FOOTBALL)
			.thumbImg("www.test.com")
			.build();

		NewsCount savedNewsCount = NewsCount.builder()
			.news(news)
			.viewCount(50)
			.build();

		executorService.submit(() -> {
			newsRepository.save(news);
			newsCountRepository.save(savedNewsCount);
		}).get();

		// when
		for (int i = 0; i < threadCount; i++) {
			executorService.execute(() -> {
				try {
					newsCountService.minusViewCont(news.getId());
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();

		// then
		assertThat(newsCountRepository.findById(savedNewsCount.getId()).get().getViewCount())
			.isEqualTo(0);
	}
}
