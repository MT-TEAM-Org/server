package org.myteam.server.admin.service;


import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.repository.AdminImprovementSearchRepo;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import static org.myteam.server.admin.dto.request.AdminMemoRequestDto.AdminMemoImprovementRequest;
import static org.myteam.server.admin.dto.response.ImprovementResponseDto.*;
import static org.myteam.server.admin.dto.request.ImproveRequestDto.*;

@Service
@RequiredArgsConstructor
public class AdminImprovementService {

    private final AdminImprovementSearchRepo adminImprovementSearchRepo;


    public Page<ResponseImprovement> getImproveListCond(RequestImprovementList requestImprovementList) {

        return adminImprovementSearchRepo.getImprovementList(requestImprovementList);
    }

    public Page<ResponseMemberImproveList> getImproveListMember(RequestMemberImproveList requestImprovementList) {

        return adminImprovementSearchRepo.getMemberImprovementList(requestImprovementList);
    }

    public ResponseImprovementDetail getImproveDetail(RequestImprovementDetail requestImprovementList) {

        return adminImprovementSearchRepo.getImprovementDetail(requestImprovementList);
    }

    public void addAdminMemo(AdminMemoImprovementRequest adminMemoRequest) {
        adminImprovementSearchRepo.createAdminMemo(adminMemoRequest);
    }

}
