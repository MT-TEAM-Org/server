package org.myteam.server.news.newsCommentMember.domain;

import org.myteam.server.global.domain.Base;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.newsComment.domain.NewsComment;

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
@Entity(name = "p_news_comment_member")
public class NewsCommentMember extends Base {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private NewsComment newsComment;

	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@Builder
	public NewsCommentMember(Long id, NewsComment newsComment, Member member) {
		this.id = id;
		this.newsComment = newsComment;
		this.member = member;
	}

	public static NewsCommentMember createEntity(NewsComment newsComment, Member member) {
		return NewsCommentMember.builder()
			.newsComment(newsComment)
			.member(member)
			.build();
	}
}
