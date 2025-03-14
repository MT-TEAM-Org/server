package org.myteam.server.news.newsReply.domain;

import org.myteam.server.global.domain.Base;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.newsComment.domain.NewsComment;

import jakarta.persistence.CascadeType;
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

	private String imgUrl;

	private int recommendCount;

	@Builder
	public NewsReply(Long id, NewsComment newsComment, Member member, String comment, String ip, String imgUrl, int recommendCount) {
		this.id = id;
		this.newsComment = newsComment;
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

	public static NewsReply createEntity(NewsComment newsComment, Member member, String comment, String ip, String imgUrl) {
		return NewsReply.builder()
			.newsComment(newsComment)
			.member(member)
			.comment(comment)
			.ip(ip)
			.imgUrl(imgUrl)
			.build();
	}
}
