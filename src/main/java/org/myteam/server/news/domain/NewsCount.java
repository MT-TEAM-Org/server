package org.myteam.server.news.domain;

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

	private int likeCount;

	private int commentCount;

	private int viewCount;

	@Builder
	public NewsCount(Long id, News news, int likeCount, int commentCount, int viewCount) {
		this.id = id;
		this.news = news;
		this.likeCount = likeCount;
		this.commentCount = commentCount;
		this.viewCount = viewCount;
	}
}
