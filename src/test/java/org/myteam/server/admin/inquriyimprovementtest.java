package org.myteam.server.admin;


import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.myteam.server.admin.entity.AdminImproveChangeLog;
import org.myteam.server.admin.entity.AdminInquiryChangeLog;
import org.myteam.server.admin.repository.AdminImprovementSearchRepo;
import org.myteam.server.admin.repository.InquirySearchRepo;
import org.myteam.server.improvement.domain.ImportantStatus;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.member.entity.Member;
import org.myteam.server.support.IntegrationTestSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.myteam.server.admin.dto.request.AdminMemoRequestDto.AdminMemoImprovementRequest;
import static org.myteam.server.admin.dto.request.AdminMemoRequestDto.AdminMemoInquiryRequest;
import static org.myteam.server.admin.dto.response.ImprovementResponseDto.*;
import static org.myteam.server.admin.dto.response.InquiryResponseDto.*;
import static org.myteam.server.admin.dto.response.InquiryResponseDto.ResponseInquiryList;
import static org.myteam.server.admin.dto.response.InquiryResponseDto.ResponseInquiryListCond;
import static org.myteam.server.admin.dto.request.ImproveRequestDto.*;
import static org.myteam.server.admin.dto.request.InquiryRequestDto.*;
import static org.myteam.server.admin.entity.QAdminImproveChangeLog.adminImproveChangeLog;
import static org.myteam.server.admin.entity.QAdminInquiryChangeLog.adminInquiryChangeLog;

@AutoConfigureMockMvc
public class inquriyimprovementtest extends IntegrationTestSupport {


    @Autowired
    AdminImprovementSearchRepo adminImprovementSearchRepo;
    @Autowired
    InquirySearchRepo inquirySearchRepo;

    Member admin;

    Improvement improvement1;
    Inquiry inquiry1;

    @Autowired
    JPAQueryFactory queryFactory;

    @BeforeEach
    void setting() {

        admin = createAdmin(1);

        improvement1 = createImprovement(admin, false);
        createImprovement(admin, false);
        createImprovement(admin, false);
        inquiry1 = createInquiry(admin);
        createInquiry(admin);
        createInquiry(admin);
    }

    @Test
    void getImproveInquriyList() {
        RequestImprovementList requestImprovementList =
                RequestImprovementList
                        .builder()
                        .improvementStatus(ImprovementStatus.PENDING)
                        .offset(1)
                        .build();


        List<ResponseImprovement> responseMemberImproveList =
                adminImprovementSearchRepo.getImprovementList(requestImprovementList).getContent();

        assertThat(responseMemberImproveList.size()).isEqualTo(3);


        RequestInquiryListCond requestInquiryListCond = RequestInquiryListCond
                .builder()
                .isAnswered(false)
                .offset(1)
                .build();

        List<ResponseInquiryListCond> requestInquiryListConds = inquirySearchRepo
                .getInquiryListByCond(requestInquiryListCond).getContent();

        assertThat(requestInquiryListConds.size()).isEqualTo(3);

    }

    @Test
    void getdetailandmemo() {
        RequestImprovementDetail requestImprovementDetail = RequestImprovementDetail
                .builder()
                .contentId(improvement1.getId())
                .build();
        RequestInquiryDetail requestInquiryDetail = RequestInquiryDetail
                .builder()
                .id(inquiry1.getId())
                .build();
        ResponseImprovementDetail responseImprovementDetail =
                adminImprovementSearchRepo.getImprovementDetail(requestImprovementDetail);
        ResponseInquiryDetail responseInquiryDetail =
                inquirySearchRepo.getInquiryDetail(requestInquiryDetail);
        assertThat(responseImprovementDetail.getAdminMemoResponseList().size()).isEqualTo(0);
        assertThat(responseInquiryDetail.getAdminMemoResponseList().size()).isEqualTo(0);
        assertThat(responseImprovementDetail.getImprovementStatus()).isEqualTo("대기");
        assertThat(responseImprovementDetail.getImportantStatus()).isEqualTo("중간");
        assertThat(responseInquiryDetail.getIsAnswered()).isEqualTo("답변대기");


        AdminMemoImprovementRequest adminMemoImprovementRequest =
                AdminMemoImprovementRequest
                        .builder()
                        .contentId(improvement1.getId())
                        .importantStatus(ImportantStatus.HIGH)
                        .improvementStatus(ImprovementStatus.COMPLETED)
                        .build();
        adminImprovementSearchRepo.createAdminMemo(adminMemoImprovementRequest);

        AdminMemoInquiryRequest adminMemoInquiryRequest = AdminMemoInquiryRequest
                .builder()
                .contentId(inquiry1.getId())
                .content("ㅋㅋㅋㅋ")
                .email(admin.getEmail())
                .build();
        inquirySearchRepo.createAdminMemo(adminMemoInquiryRequest);
        responseImprovementDetail =
                adminImprovementSearchRepo.getImprovementDetail(requestImprovementDetail);
        responseInquiryDetail =
                inquirySearchRepo.getInquiryDetail(requestInquiryDetail);
        assertThat(responseImprovementDetail.getAdminMemoResponseList().size()).isEqualTo(0);
        assertThat(responseInquiryDetail.getAdminMemoResponseList().size()).isEqualTo(1);
        assertThat(responseImprovementDetail.getImprovementStatus()).isEqualTo("완료");
        assertThat(responseImprovementDetail.getImportantStatus()).isEqualTo("높음");
        assertThat(responseInquiryDetail.getIsAnswered()).isEqualTo("답변완료");

        List<AdminImproveChangeLog> adminImproveChangeLogs=
                queryFactory.selectFrom(adminImproveChangeLog)
                        .where(adminImproveChangeLog.contentId.eq(improvement1.getId()))
                        .fetch();

        List<AdminInquiryChangeLog> adminInquiryChangeLogs=
                queryFactory.selectFrom(adminInquiryChangeLog)
                        .where(adminInquiryChangeLog.contentId.eq(inquiry1.getId()))
                        .fetch();

        assertThat(adminImproveChangeLogs.size()).isEqualTo(1);
        assertThat(adminInquiryChangeLogs.size()).isEqualTo(1);

        inquirySearchRepo.createAdminMemo(adminMemoInquiryRequest);

        adminInquiryChangeLogs=
                queryFactory.selectFrom(adminInquiryChangeLog)
                        .where(adminInquiryChangeLog.contentId.eq(inquiry1.getId()))
                        .fetch();

        assertThat(adminInquiryChangeLogs.size()).isEqualTo(1);
    }

    @Test
    void addMemberImproveInquriy() {
        RequestMemberImproveList requestMemberImproveList = RequestMemberImproveList
                .builder()
                .publicId(admin.getPublicId())
                .offset(1)
                .build();
        List<ResponseMemberImproveList> responseMemberImproveListList =
                adminImprovementSearchRepo.getMemberImprovementList(requestMemberImproveList).getContent();
        assertThat(responseMemberImproveListList.size()).isEqualTo(3);
        RequestInquiryList requestInquiryList = RequestInquiryList
                .builder()
                .email(admin.getEmail())
                .offset(1)
                .build();

        List<ResponseInquiryList> responseInquiryLists = inquirySearchRepo.getInquiryList(requestInquiryList)
                .getContent();

        assertThat(responseInquiryLists.size()).isEqualTo(3);
    }

}
