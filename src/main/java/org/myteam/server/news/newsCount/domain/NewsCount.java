package org.myteam.server.news.newsCount.domain;

import org.myteam.server.news.news.domain.News;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "p_news_count")
public class NewsCount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	private News news;

	private int recommendCount;

	private int commentCount;

	private int viewCount;

	@Builder
	public NewsCount(Long id, News news, int recommendCount, int commentCount, int viewCount) {
		this.id = id;
		this.news = news;
		this.recommendCount = recommendCount;
		this.commentCount = commentCount;
		this.viewCount = viewCount;
	}

	public NewsCount addRecommendCount() {
		this.recommendCount++;
		return this;
	}
}
