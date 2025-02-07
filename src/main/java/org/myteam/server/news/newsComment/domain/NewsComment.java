package org.myteam.server.news.newsComment.domain;

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
@Entity(name = "p_news_comment")
public class NewsComment extends Base {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private News news;

	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	private String comment;

	private String ip;

	@Builder
	public NewsComment(Long id, News news, Member member, String comment, String ip) {
		this.id = id;
		this.news = news;
		this.member = member;
		this.comment = comment;
		this.ip = ip;
	}

	public void confirmMember(Member member) {
		this.member.confirmMemberEquals(member);
	}

	public void updateComment(String comment) {
		this.comment = comment;
	}

	public static NewsComment createNewsComment(News news, Member member, String comment, String ip) {
		return NewsComment.builder()
			.news(news)
			.member(member)
			.comment(comment)
			.ip(ip)
			.build();
	}
}
