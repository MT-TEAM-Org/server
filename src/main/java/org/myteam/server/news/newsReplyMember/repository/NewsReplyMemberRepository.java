package org.myteam.server.news.newsReplyMember.repository;

import java.util.Optional;
import java.util.UUID;

import org.myteam.server.news.newsReplyMember.domain.NewsReplyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsReplyMemberRepository extends JpaRepository<NewsReplyMember, Long> {

	Optional<NewsReplyMember> findByNewsReplyIdAndMemberPublicId(Long newsReplyId, UUID memberId);

	void deleteByNewsReplyIdAndMemberPublicId(Long newsReplyId, UUID memberId);

}
