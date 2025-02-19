package org.myteam.server.news.newsCommentMember.repository;

import java.util.Optional;
import java.util.UUID;

import org.myteam.server.news.newsCommentMember.domain.NewsCommentMember;
import org.myteam.server.news.newsCountMember.domain.NewsCountMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsCommentMemberRepository extends JpaRepository<NewsCommentMember, Long> {

	Optional<NewsCommentMember> findByNewsCommentIdAndMemberPublicId(Long newsCommentId, UUID memberId);

	void deleteByNewsCommentIdAndMemberPublicId(Long newsCommentId, UUID memberId);

}
