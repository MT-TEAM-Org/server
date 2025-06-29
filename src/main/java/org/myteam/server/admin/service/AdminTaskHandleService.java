package org.myteam.server.admin.service;


import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.repository.AdminTaskHandleRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import static org.myteam.server.admin.dto.AdminTask.*;

@Service
@RequiredArgsConstructor
public class AdminTaskHandleService {


    private final AdminTaskHandleRepository adminTaskHandleRepository;


   public String handleRequestInquiryImprovement(RequestHandleInquiryImprovement requestHandleInquiry,String ip){

;

        return adminTaskHandleRepository.handleImprovementInquiry(requestHandleInquiry,ip);

    }

    public String ContentAdminControlTaskHandle(RequestHandleContent requestHandleContent){


        return adminTaskHandleRepository.ContentAdminControlTaskHandle(requestHandleContent);
    }


    public String ContentMemberControlTaskHandle(RequestHandleMember requestHandleMember){


        return adminTaskHandleRepository.handleMemberControlTask(requestHandleMember);
    }


}
