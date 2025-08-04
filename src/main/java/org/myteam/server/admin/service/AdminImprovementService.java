package org.myteam.server.admin.service;


import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.repository.AdminImprovementSearchRepo;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.global.util.redis.service.RedisService;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import static org.myteam.server.admin.dto.request.AdminMemoRequestDto.AdminMemoImprovementRequest;
import static org.myteam.server.admin.dto.response.ImprovementResponseDto.*;
import static org.myteam.server.admin.dto.request.ImproveRequestDto.*;

@Service
@RequiredArgsConstructor
public class AdminImprovementService {

    private final AdminImprovementSearchRepo adminImprovementSearchRepo;
    private final SecurityReadService securityReadService;
    private final RedisService redisService;


    public Page<ResponseImprovement> getImproveListCond(RequestImprovementList requestImprovementList) {

        return adminImprovementSearchRepo.getImprovementList(requestImprovementList);
    }

    public Page<ResponseMemberImproveList> getImproveListMember(RequestMemberImproveList requestImprovementList) {

        return adminImprovementSearchRepo.getMemberImprovementList(requestImprovementList);
    }

    public ResponseImprovementDetail getImproveDetail(RequestImprovementDetail requestImprovementList) {
        ResponseImprovementDetail responseImprovementDetail
                =adminImprovementSearchRepo.getImprovementDetail(requestImprovementList);;
        if(requestImprovementList.getAlarmCheck()!=null) {
            Member admin = securityReadService.getMember();
            redisService.adminReadCheckUpdate(admin.getPublicId().toString()
                    , StaticDataType.Improvement, requestImprovementList.getContentId());
        }
        return responseImprovementDetail;
    }

    public void addAdminMemo(AdminMemoImprovementRequest adminMemoRequest) {
        adminImprovementSearchRepo.createAdminMemo(adminMemoRequest);
    }

}
