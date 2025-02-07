package org.myteam.server.news.domain;

import org.myteam.server.global.domain.Base;
import org.myteam.server.member.entity.Member;

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
@Entity(name = "p_news_reply")
public class NewsReply extends Base {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private NewsComment newsComment;

	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	private String comment;

	private String ip;

	@Builder
	public NewsReply(Long id, NewsComment newsComment, Member member, String comment, String ip) {
		this.id = id;
		this.newsComment = newsComment;
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

	public static NewsReply createNewsReply(NewsComment newsComment, Member member, String comment, String ip) {
		return NewsReply.builder()
			.newsComment(newsComment)
			.member(member)
			.comment(comment)
			.ip(ip)
			.build();
	}
}
