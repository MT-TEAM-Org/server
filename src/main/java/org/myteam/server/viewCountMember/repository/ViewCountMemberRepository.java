package org.myteam.server.viewCountMember.repository;

import java.util.UUID;

import org.myteam.server.viewCountMember.domain.ViewCountMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewCountMemberRepository extends JpaRepository<ViewCountMember, Long> {
	boolean existsByViewIdAndMemberPublicId(Long viewId, UUID memberId);
}
