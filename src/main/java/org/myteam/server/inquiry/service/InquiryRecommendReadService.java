package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.inquiry.repository.InquiryRecommendRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InquiryRecommendReadService {
    private final InquiryRecommendRepository inquiryRecommendRepository;

    // TODO: memberId가 없을때 생각
    public void confirmExistInquiryRecommend(Long inquiryId, UUID memberId) {
        inquiryRecommendRepository.findByInquiryIdAndMemberPublicId(inquiryId, memberId)
                .ifPresent(member -> {
                    throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND_BOARD);
                });
    }

    // TODO: memberId가 없을때 생각
    public boolean isAlreadyRecommended(Long inquiryId, UUID publicId) {
        if (!inquiryRecommendRepository.findByInquiryIdAndMemberPublicId(inquiryId, publicId).isPresent()) {
            throw new PlayHiveException(ErrorCode.NO_MEMBER_RECOMMEND_RECORD);
        }
        return true;
    }

    // TODO: memberId가 없을때 생각
    public boolean isRecommended(Long inquiryId, UUID publicId) {
        return inquiryRecommendRepository.findByInquiryIdAndMemberPublicId(inquiryId, publicId).isPresent();
    }
}
