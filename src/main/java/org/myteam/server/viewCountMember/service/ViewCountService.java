package org.myteam.server.viewCountMember.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.myteam.server.global.cookie.CookieHandler;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.viewCountMember.domain.ViewCountMember;
import org.myteam.server.viewCountMember.domain.ViewType;
import org.myteam.server.viewCountMember.repository.ViewCountMemberRepository;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ViewCountService {

	private final CookieHandler cookieHandler;
	private final ViewCountMemberRepository viewCountMemberRepository;
	private final ViewCountMemberReadService viewCountMemberReadService;
	private final MemberReadService memberReadService;

	public boolean confirmPostView(HttpServletRequest request, HttpServletResponse response, ViewType viewType,
		Long id, UUID publicId) {
		if (publicId != null) {
			if (!viewCountMemberReadService.existsByViewIdAndMemberPublicId(id, publicId)) {
				save(viewType, id, publicId);
				return false;
			}
			return true;
		}
		return checkCookiePostView(request, response, viewType, id);
	}

	public void save(ViewType viewType, Long id, UUID publicId) {
		viewCountMemberRepository.save(
			ViewCountMember.createEntity(id, memberReadService.findById(publicId), viewType));
	}

	private boolean checkCookiePostView(HttpServletRequest request, HttpServletResponse response, ViewType viewType,
		Long id) {
		String postViewCookieName = "postView_" + viewType.getValue();
		String postViewCookieValue = cookieHandler.getCookieValue(request, postViewCookieName);

		List<String> postViewList = new ArrayList<>();
		boolean hasViewed = cookieHandler.hasPostBeenViewed(id, postViewCookieValue, postViewList);

		cookieHandler.updatePostViewCookie(response, id, hasViewed, postViewCookieName, postViewList);

		return hasViewed;
	}
}
