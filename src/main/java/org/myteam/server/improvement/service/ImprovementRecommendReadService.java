package org.myteam.server.improvement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.improvement.repository.ImprovementRecommendRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImprovementRecommendReadService {

    private final ImprovementRecommendRepository improvementRecommendRepository;

    public boolean isRecommended(Long improvementId, UUID memberPublicId) {
        return improvementRecommendRepository.findByImprovementIdAndMemberPublicId(improvementId, memberPublicId).isPresent();
    }
}
