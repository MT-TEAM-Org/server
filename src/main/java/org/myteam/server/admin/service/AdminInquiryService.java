package org.myteam.server.admin.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.entity.AdminContentMemo;
import org.myteam.server.admin.repository.InquirySearchRepo;
import org.myteam.server.admin.utill.enums.StaticDataType;
import org.myteam.server.common.certification.service.InquiryAnsSendService;
import org.myteam.server.global.util.redis.service.RedisService;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import static org.myteam.server.admin.dto.request.AdminMemoRequestDto.AdminMemoInquiryRequest;
import static org.myteam.server.admin.dto.response.InquiryResponseDto.*;
import static org.myteam.server.admin.dto.response.InquiryResponseDto.ResponseInquiryList;
import static org.myteam.server.admin.dto.response.InquiryResponseDto.ResponseInquiryListCond;
import static org.myteam.server.admin.dto.request.InquiryRequestDto.*;


@Service
@RequiredArgsConstructor
public class AdminInquiryService {

    private final InquirySearchRepo inquirySearchRepo;
    private final InquiryAnsSendService inquiryAnsSendStrategy;
    private final MemberReadService memberReadService;
    private final SecurityReadService securityReadService;
    private final RedisService redisService;

    public Page<ResponseInquiryListCond> getInquiryListCond(RequestInquiryListCond requestInquiryListCond) {

        return inquirySearchRepo.getInquiryListByCond(requestInquiryListCond);
    }

    public ResponseInquiryDetail getInquiryDetail(Long inquiryId,String readCheck) {
        ResponseInquiryDetail responseInquiryDetail=
                inquirySearchRepo.getInquiryDetail(inquiryId);
        if(readCheck!=null) {
            Member admin = securityReadService.getMember();
            redisService.adminReadCheckUpdate(admin.getPublicId().toString()
                    , StaticDataType.Inquiry, inquiryId);
        }
        return responseInquiryDetail;
    }

    public Page<ResponseInquiryList> getInquiryListMember(RequestInquiryList requestInquiryDetail) {

        return inquirySearchRepo.getInquiryList(requestInquiryDetail);
    }

    public void sendInquiryAnswer(AdminMemoInquiryRequest adminMemoRequest) {
        AdminContentMemo adminMemo = inquirySearchRepo.createAdminMemo(adminMemoRequest);
        if (adminMemo != null) {
            inquiryAnsSendStrategy.send(adminMemo,
                    memberReadService.getByEmail(adminMemoRequest.getEmail()));
        }
    }
}
