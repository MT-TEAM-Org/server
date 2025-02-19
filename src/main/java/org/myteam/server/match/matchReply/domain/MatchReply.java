package org.myteam.server.match.matchReply.domain;

import org.myteam.server.global.domain.Base;
import org.myteam.server.match.matchComment.domain.MatchComment;
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
@Entity(name = "p_match_reply")
public class MatchReply extends Base {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private MatchComment matchComment;

	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	private String comment;

	private String ip;

	private String imgUrl;

	@Builder
	public MatchReply(Long id, MatchComment matchComment, Member member, String comment, String ip, String imgUrl) {
		this.id = id;
		this.matchComment = matchComment;
		this.member = member;
		this.comment = comment;
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

	public static MatchReply createEntity(MatchComment matchComment, Member member, String comment, String ip, String imgUrl) {
		return MatchReply.builder()
			.matchComment(matchComment)
			.member(member)
			.comment(comment)
			.ip(ip)
			.imgUrl(imgUrl)
			.build();
	}
}
