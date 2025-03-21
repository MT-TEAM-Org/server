package org.myteam.server.viewCountMember.domain;

import org.myteam.server.global.domain.Base;
import org.myteam.server.member.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Entity
public class ViewCountMember extends Base {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long viewId;

	@OneToOne
	private Member member;

	@Enumerated(EnumType.STRING)
	private ViewType viewType;

	@Builder
	public ViewCountMember(Long id, Long viewId, Member member, ViewType viewType) {
		this.id = id;
		this.viewId = viewId;
		this.member = member;
		this.viewType = viewType;
	}

	public static ViewCountMember createEntity(Long viewId, Member member, ViewType viewType) {
		return ViewCountMember.builder()
			.viewId(viewId)
			.member(member)
			.viewType(viewType)
			.build();
	}
}
