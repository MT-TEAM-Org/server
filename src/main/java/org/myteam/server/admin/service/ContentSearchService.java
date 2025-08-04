package org.myteam.server.admin.service;


import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.repository.ContentSearchRepository;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.global.util.redis.service.RedisService;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static org.myteam.server.admin.dto.request.AdminMemoRequestDto.*;
import static org.myteam.server.admin.dto.request.ContentRequestDto.*;
import static org.myteam.server.admin.dto.response.ResponseContentDto.*;

@Service
@Repository
@RequiredArgsConstructor
public class ContentSearchService {

    private final ContentSearchRepository contentSearchRepository;
    private final SecurityReadService securityReadService;
    private final RedisService redisService;

    public Page<ResponseReportList> getReportList(RequestReportList requestReportList){

        return contentSearchRepository.getReportList(requestReportList);
    }
    public Page<ResponseContentSearch> getContentList(RequestContentData requestReportList){

        return contentSearchRepository.getDataList(requestReportList);
    }
    public ResponseDetail getContentDetail(RequestDetail requestDetail){
        ResponseDetail responseDetail=contentSearchRepository.getDetail(requestDetail);
        if(requestDetail.getAlarmCheck()!=null&&requestDetail.getReportId()!=null) {
            Member admin = securityReadService.getMember();
            redisService.adminReadCheckUpdate(admin.getPublicId().toString()
                    , StaticDataType.Report, requestDetail.getReportId());
        }
        return responseDetail;
    }
    public void addAdminMemo(AdminMemoContentRequest adminMemoContentRequest){
        contentSearchRepository.addAdminMemo(adminMemoContentRequest);
    }

}
