package org.myteam.server.news.newsReply.service;

import java.util.UUID;

import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.newsComment.domain.NewsComment;
import org.myteam.server.news.newsComment.service.NewsCommentReadService;
import org.myteam.server.news.newsReply.domain.NewsReply;
import org.myteam.server.news.newsReply.dto.service.request.NewsReplySaveServiceRequest;
import org.myteam.server.news.newsReply.dto.service.request.NewsReplyUpdateServiceRequest;
import org.myteam.server.news.newsReply.dto.service.response.NewsReplyResponse;
import org.myteam.server.news.newsReply.repository.NewsReplyRepository;
import org.myteam.server.news.newsReplyMember.service.NewsReplyMemberReadService;
import org.myteam.server.news.newsReplyMember.service.NewsReplyMemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsReplyService {

	private final NewsReplyRepository newsReplyRepository;
	private final NewsReplyReadService newsReplyReadService;
	private final NewsCommentReadService newsCommentReadService;
	private final SecurityReadService securityReadService;
	private final NewsReplyMemberReadService newsReplyMemberReadService;
	private final NewsReplyMemberService newsReplyMemberService;
	private final MemberReadService memberReadService;

	public NewsReplyResponse save(NewsReplySaveServiceRequest newsReplySaveServiceRequest) {
		NewsComment newsComment = newsCommentReadService.findById(newsReplySaveServiceRequest.getNewsCommentId());
		Member member = securityReadService.getMember();

		Member mentionedMember = findMentionedMember(newsReplySaveServiceRequest.getMentionedPublicId());

		return NewsReplyResponse.createResponse(
			newsReplyRepository.save(
				NewsReply.createEntity(newsComment, member, newsReplySaveServiceRequest.getComment(),
					newsReplySaveServiceRequest.getIp(), newsReplySaveServiceRequest.getImgUrl(), mentionedMember)),
			member
		);
	}

	public Long update(NewsReplyUpdateServiceRequest newsReplyUpdateServiceRequest) {
		Member member = securityReadService.getMember();
		NewsReply newsReply = newsReplyReadService.findById(newsReplyUpdateServiceRequest.getNewsReplyId());

		newsReply.confirmMember(member);
		newsReply.update(newsReplyUpdateServiceRequest.getComment(), newsReplyUpdateServiceRequest.getImgUrl());

		Member mentionedMember = findMentionedMember(newsReplyUpdateServiceRequest.getMentionedPublicId());
		newsReply.updateMentionedMember(mentionedMember);

		return newsReply.getId();
	}

	public Long delete(Long newsReplyId) {
		Member member = securityReadService.getMember();
		NewsReply newsReply = newsReplyReadService.findById(newsReplyId);

		newsReply.confirmMember(member);

		newsReplyRepository.deleteById(newsReplyId);
		return newsReply.getId();
	}

	public Long recommend(Long newsReplyId) {
		Member member = securityReadService.getMember();
		NewsReply newsReply = newsReplyReadService.findByIdLock(newsReplyId);

		newsReplyMemberReadService.confirmExistMember(newsReplyId, member.getPublicId());

		newsReplyMemberService.save(newsReplyId);

		newsReply.addRecommendCount();

		return newsReply.getId();
	}

	public Long cancelRecommend(Long newsReplyId) {
		Member member = securityReadService.getMember();
		NewsReply newsReply = newsReplyReadService.findByIdLock(newsReplyId);

		newsReplyMemberReadService.confirmExistMember(newsReplyId, member.getPublicId());

		newsReplyMemberService.deleteByNewsReplyIdMemberId(newsReplyId);

		newsReply.minusRecommendCount();

		return newsReply.getId();
	}

	private Member findMentionedMember(UUID publicId) {
		if (publicId != null) {
			return memberReadService.findById(publicId);
		}
		return null;
	}
}
