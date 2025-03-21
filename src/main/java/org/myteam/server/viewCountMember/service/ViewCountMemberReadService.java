package org.myteam.server.viewCountMember.service;

import java.util.UUID;

import org.myteam.server.viewCountMember.repository.ViewCountMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ViewCountMemberReadService {

	private final ViewCountMemberRepository viewCountMemberRepository;

	public boolean existsByViewIdAndMemberPublicId(Long viewId, UUID publicId) {
		return viewCountMemberRepository.existsByViewIdAndMemberPublicId(viewId, publicId);
	}

}
