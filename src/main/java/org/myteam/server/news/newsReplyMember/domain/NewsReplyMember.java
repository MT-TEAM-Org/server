package org.myteam.server.news.newsReplyMember.domain;

import org.myteam.server.global.domain.Base;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.newsReply.domain.NewsReply;

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
@Entity(name = "p_news_reply_member")
public class NewsReplyMember extends Base {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private NewsReply newsReply;

	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@Builder
	public NewsReplyMember(Long id, NewsReply newsReply, Member member) {
		this.id = id;
		this.newsReply = newsReply;
		this.member = member;
	}

	public static NewsReplyMember createEntity(NewsReply newsReply, Member member) {
		return NewsReplyMember.builder()
			.newsReply(newsReply)
			.member(member)
			.build();
	}
}
