package org.myteam.server.match.matchComment.domain;

import org.myteam.server.global.domain.Base;
import org.myteam.server.match.match.domain.Match;
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
@Entity(name = "p_match_comment")
public class MatchComment extends Base {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Match match;

	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	private String comment;

	private String recommendCount;

	private String ip;

	private String imgUrl;

	@Builder
	public MatchComment(Long id, Match match, Member member, String comment, String recommendCount, String ip, String imgUrl) {
		this.id = id;
		this.match = match;
		this.member = member;
		this.comment = comment;
		this.recommendCount = recommendCount;
		this.ip = ip;
		this.imgUrl = imgUrl;
	}

	public void confirmMember(Member member) {
		this.member.confirmMemberEquals(member);
	}

	public void update(String comment, String imgUrl) {
		updateComment(comment);
		updateImgUrl(imgUrl);
	}

	private void updateComment(String comment) {
		this.comment = comment;
	}

	private void updateImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public void addRecommendCount() {
		this.recommendCount += 1;
	}

	public static MatchComment createEntity(Match match, Member member, String comment, String ip, String imgUrl) {
		return MatchComment.builder()
			.match(match)
			.member(member)
			.comment(comment)
			.ip(ip)
			.imgUrl(imgUrl)
			.build();
	}
}
