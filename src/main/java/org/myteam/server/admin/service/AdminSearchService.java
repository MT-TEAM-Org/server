package org.myteam.server.admin.service;


import feign.Request;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.dto.AdminDetail;
import org.myteam.server.admin.dto.AdminSearch;
import org.myteam.server.admin.repository.AdminSearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import static org.myteam.server.admin.dto.AdminSearch.*;

@Service
@RequiredArgsConstructor
public class AdminSearchService {

    private final AdminSearchRepository adminSearchRepository;
    public Page<ResponseSearchContent> getCommentBoardList(RequestSearchContent requestSearchContent){
        return adminSearchRepository.getCommentBoardList(requestSearchContent);

    }


    public Page<ResponseSearchInquiryImprovement> getInquiryImprovementList(RequestSearchInquiryImprovement
                                                                                    requestSearchInquiryImprovement){

        return adminSearchRepository.getInquiryImprovementList(requestSearchInquiryImprovement);
    }

    public Page<ResponseSearchUserList> getUserSearchList(RequestSearchUserList requestSearchUserList){


        return adminSearchRepository.getUserInfoList(requestSearchUserList);
    }

    public Page<ResponseUserReportedList> getUserReportedList(RequestUserReportedList requestUserReportedList){
        return adminSearchRepository.getUserReportedList(requestUserReportedList);
    }

    public Page<ResponseReportList> getReportList(RequestReportList requestReportList){

        return adminSearchRepository.getReportList(requestReportList);
    }


}
