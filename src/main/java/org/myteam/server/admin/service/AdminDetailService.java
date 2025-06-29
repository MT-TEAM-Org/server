package org.myteam.server.admin.service;


import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.dto.AdminDetail;
import org.myteam.server.admin.dto.AdminSearch;
import org.myteam.server.admin.repository.AdminDetailRepository;
import org.myteam.server.admin.utils.StaticDataType;
import org.myteam.server.improvement.repository.ImprovementQueryRepository;
import org.myteam.server.improvement.repository.ImprovementRepository;
import org.myteam.server.inquiry.repository.InquiryQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import static org.myteam.server.admin.dto.AdminDetail.*;
import static org.myteam.server.admin.dto.AdminSearch.*;

@Service
@RequiredArgsConstructor
public class AdminDetailService {

    private final AdminDetailRepository adminDetailRepository;


    public ResponseContentDetail getCommentBoardDetail(RequestContentDetail requestContentDetail){


       return adminDetailRepository.getCommentBoardDetail(requestContentDetail);

    }


    public ResponseClient getRquestClientDetail(RequestClient requestClient){

        return adminDetailRepository.getRequestClientDetail(requestClient);


    }

    public ResponseUserReportDetail getUserReportedDetail(RequestUserReportDetail requestUserReportDetail){


        return adminDetailRepository.getUserReportedDetail(requestUserReportDetail);

    }

    public ResponseUserDetail getUserDetail(RequestUserDetail requestUserDetail){

        return adminDetailRepository.getUserDetail(requestUserDetail);
    }
}
