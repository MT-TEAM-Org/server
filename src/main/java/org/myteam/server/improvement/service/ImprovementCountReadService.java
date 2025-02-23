package org.myteam.server.improvement.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.improvement.domain.ImprovementCount;
import org.myteam.server.improvement.repository.ImprovementCountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImprovementCountReadService {

    private final ImprovementCountRepository improvementCountRepository;

    public ImprovementCount findByImprovementId(Long improvementId) {
        return improvementCountRepository.findByImprovementId(improvementId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.IMPROVEMENT_COUNT_NOT_FOUND));
    }
}
