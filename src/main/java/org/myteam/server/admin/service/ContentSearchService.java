package org.myteam.server.admin.service;


import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.repository.ContentSearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import static org.myteam.server.admin.dto.RequestContentDto.*;
import static org.myteam.server.admin.dto.ResponseContentDto.*;

@Service
@Repository
@RequiredArgsConstructor
public class ContentSearchService {

    private final ContentSearchRepository contentSearchRepository;

    public Page<ResponseReportList> getReportList(RequestReportList requestReportList){

        return contentSearchRepository.getReportList(requestReportList);
    }
    public Page<ResponseContentSearch> getContentList(RequestContentData requestReportList){

        return contentSearchRepository.getDataList(requestReportList);
    }
    public ResponseDetail getContentDetail(RequestDetail requestDetail){

        return contentSearchRepository.getDetail(requestDetail);
    }
    public void addAdminMemo(AdminMemoRequest adminMemoRequest){
        contentSearchRepository.addAdminMemo(adminMemoRequest);
    }

}
