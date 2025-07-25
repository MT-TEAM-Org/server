package org.myteam.server.admin.service;


import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.repository.AdminMemberResearchRepo;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import static org.myteam.server.admin.dto.MemberSearchRequestDto.RequestMemberDetail;
import static org.myteam.server.admin.dto.MemberSearchRequestDto.RequestMemberSearch;
import static org.myteam.server.admin.dto.MemberSearchResponseDto.*;

@Service
@RequiredArgsConstructor
public class AdminMemberSearchService {

    private final AdminMemberResearchRepo adminMemberResearchRepo;

    public Page<ResponseMemberSearch> getMemberDataList(RequestMemberSearch requestMemberSearch) {

        return adminMemberResearchRepo.getMemberDataList(requestMemberSearch);

    }

    public ResponseMemberDetail getMemberDetailData(RequestMemberDetail requestMemberDetail) {

        return adminMemberResearchRepo.getMemberDetail(requestMemberDetail.getPublicId());
    }


    public Page<ResponseReportList> getMemberReportedList(RequestMemberDetail requestMemberDetail) {

        return adminMemberResearchRepo.getMemberReportedList(requestMemberDetail);
    }


}
