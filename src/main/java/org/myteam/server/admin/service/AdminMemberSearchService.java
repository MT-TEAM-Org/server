package org.myteam.server.admin.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.dto.request.MemberSearchRequestDto;
import org.myteam.server.admin.dto.response.MemberSearchResponseDto;
import org.myteam.server.admin.repository.AdminMemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import static org.myteam.server.admin.dto.request.MemberSearchRequestDto.*;
import static org.myteam.server.admin.dto.response.MemberSearchResponseDto.*;

@Service
@RequiredArgsConstructor
public class AdminMemberSearchService {
    private final AdminMemberRepository adminMemberRepository;
    public Page<ResponseMemberSearch> getMemberDataList(RequestMemberSearch requestMemberSearch) {

        return adminMemberRepository.getMemberDataList(requestMemberSearch);
    }
    public ResponseMemberDetail getMemberDetailData(RequestMemberDetail requestMemberDetail) {

        return adminMemberRepository.getMemberDetail(requestMemberDetail.getPublicId());
    }
    public Page<ResponseReportList> getMemberReportedList(RequestMemberDetail requestMemberDetail) {

        return adminMemberRepository.getMemberReportedList(requestMemberDetail);
    }
}
