package org.myteam.server.news.newsCountMember.repository;

import java.util.Optional;

import org.myteam.server.news.newsCountMember.domain.NewsCountMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsCountMemberRepository extends JpaRepository<NewsCountMember, Long> {

	Optional<NewsCountMember> findByNewsIdAndMemberId(Long newsId, Long memberId);

}
