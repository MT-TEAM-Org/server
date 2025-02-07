package org.myteam.server.news.newsCountMember.repository;

import java.util.Optional;
import java.util.UUID;

import org.myteam.server.news.newsCountMember.domain.NewsCountMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsCountMemberRepository extends JpaRepository<NewsCountMember, Long> {

	Optional<NewsCountMember> findByNewsIdAndMemberPublicId(Long newsId, UUID memberId);

	void deleteByNewsIdAndMemberPublicId(Long newsId, UUID memberId);

}
