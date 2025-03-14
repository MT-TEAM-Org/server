package org.myteam.server.news.newsComment.domain;

import java.util.ArrayList;
import java.util.List;

import org.myteam.server.global.domain.Base;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.newsCommentMember.domain.NewsCommentMember;
import org.myteam.server.news.newsReply.domain.NewsReply;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

	private String imgUrl;

	private int recommendCount;

	@OneToMany(mappedBy = "newsComment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<NewsCommentMember> newsCommentMemberList = new ArrayList<>();

	@OneToMany(mappedBy = "newsComment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<NewsReply> newsReplyList = new ArrayList<>();

	@Builder
	public NewsComment(Long id, News news, Member member, String comment, String ip, String imgUrl,
		int recommendCount) {
		this.id = id;
		this.news = news;
		this.member = member;
		this.comment = comment;
		this.ip = ip;
		this.imgUrl = imgUrl;
		this.recommendCount = recommendCount;
	}

	public void confirmMember(Member member) {
		this.member.confirmMemberEquals(member);
	}

	public void addRecommendCount() {
		this.recommendCount += 1;
	}

	public void minusRecommendCount() {
		this.recommendCount -= 1;
	}

	public void update(String comment, String imgUrl) {
		updateComment(comment);
		updateImgUrl(imgUrl);
	}

	public void updateComment(String comment) {
		this.comment = comment;
	}

	public void updateImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public static NewsComment createEntity(News news, Member member, String comment, String ip, String imgUrl) {
		return NewsComment.builder()
			.news(news)
			.member(member)
			.comment(comment)
			.ip(ip)
			.imgUrl(imgUrl)
			.build();
	}
}
