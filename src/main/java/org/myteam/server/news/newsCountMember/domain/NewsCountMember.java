package org.myteam.server.news.newsCountMember.domain;

import org.myteam.server.global.domain.Base;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "p_news_count_member")
public class NewsCountMember extends Base {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private News news;

	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@Builder
	public NewsCountMember(Long id, News news, Member member) {
		this.id = id;
		this.news = news;
		this.member = member;
	}

	public NewsCountMember createEntity(News news, Member member) {
		return NewsCountMember.builder()
			.news(news)
			.member(member)
			.build();
	}
}
