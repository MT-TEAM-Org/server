package org.myteam.server.admin.repository;


import co.elastic.clients.json.JsonpUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.lettuce.core.ScriptOutputType;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.myteam.server.admin.document.ContentDocument;
import org.myteam.server.admin.document.ImprovementDocument;
import org.myteam.server.admin.document.InquiryDocument;
import org.myteam.server.admin.dto.request.ContentRequestDto;
import org.myteam.server.admin.dto.request.ImproveRequestDto;
import org.myteam.server.admin.dto.request.InquiryRequestDto;
import org.myteam.server.admin.dto.response.ImprovementResponseDto;
import org.myteam.server.admin.dto.response.InquiryResponseDto;
import org.myteam.server.admin.dto.response.ResponseContentDto;
import org.myteam.server.admin.repository.simpleRepo.ElasticContentRepository;
import org.myteam.server.admin.repository.simpleRepo.ElasticImproverRepository;
import org.myteam.server.admin.repository.simpleRepo.ElasticInquiryRepository;
import org.myteam.server.admin.utill.enums.AdminControlType;
import org.myteam.server.admin.utill.enums.StaticDataType;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.myteam.server.improvement.domain.ImportantStatus;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.improvement.repository.ImprovementQueryRepository;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.member.entity.Member;
import org.myteam.server.support.IntegrationTestSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.myteam.server.admin.dto.request.ContentRequestDto.*;
import static org.myteam.server.admin.dto.response.ResponseContentDto.*;
import static org.myteam.server.board.domain.QBoard.board;

@SpringBootTest
public class ElasticSearchTest extends IntegrationTestSupport {
    static private DataSource testDataSource;

    @Autowired
    ContentSearchRepository contentSearchRepository;
    @Autowired
    InquirySearchRepo inquirySearchRepo;
    @Autowired
    AdminImprovementSearchRepo adminImprovementSearchRepo;

    @Autowired
    JPAQueryFactory queryFactory;

    Member m;
    //@BeforeAll
    static void setupH2CustomFunctions(@Autowired DataSource dataSource) {
        testDataSource = dataSource; //
        try (Connection conn = testDataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE ALIAS IF NOT EXISTS DATE_FORMAT FOR 'org.myteam.server.admin.utill.StaticUtil.dateFormat'");
            System.out.println("H2에 DATE_FORMAT 함수 별칭이 성공적으로 등록되었습니다.");
        } catch (SQLException e) {
            System.err.println("H2 함수 별칭 등록 중 오류 발생: " + e.getMessage());
            e.printStackTrace();

            throw new RuntimeException("Failed to register H2 DATE_FORMAT alias", e);
        }
    }


    //@BeforeEach
    void setBeforeTest(){
        m=createMember(0);

        for(int i=0;10>i;i++){
            if(i%2==0){
                Board b=createBoard(m, Category.ESPORTS, CategoryType.FREE,"hello","오늘은 날씨가 매우 맑아서 공원에 나가 산책을 하기로 했다. 새들이 지저귀는 소리를 들으며 길을 걷다 보면 마음이 편안해진다. hello 친구들과 함께 커피를 마시며 이야기를 나누는 시간도 즐겁다. 점심으로는 간단하게 김밥과 떡볶이를 먹고," +
                        " 오후에는 책을 읽으며 휴식을 취했다. 저녁이 되자 노을이 아름답게 물들어 하루를 마무리했다.");
                Inquiry inquiry=createInquiry(m);
                Improvement improvement=createImprovement(m,false);
                ContentDocument c
                        =ContentDocument
                        .builder()
                        .staticDataType(StaticDataType.BOARD)
                        .adminControlType(AdminControlType.HIDDEN)
                        .title("hello")
                        .contentId(b.getId())
                        .content(b.getContent())
                        .createDate(b.getCreateDate())
                        .reportCount(0L)
                        .email(m.getEmail())
                        .build();
                InquiryDocument inquiryDoc=InquiryDocument.builder()
                        .id(inquiry.getId())
                        .content(inquiry.getContent())
                        .isAdminAnswered(inquiry.isAdminAnswered())
                        .isMember(true)
                        .createDate(inquiry.getCreatedAt())
                        .email(inquiry.getEmail())
                        .build();
                ImprovementDocument improvementDocument=ImprovementDocument.builder()
                        .id(improvement.getId())
                        .improvementStatus(ImprovementStatus.PENDING)
                        .nickName(m.getNickname())
                        .content(improvement.getContent())
                        .title(improvement.getTitle())
                        .importantStatus(ImportantStatus.NORMAL)
                        .createDate(improvement.getCreateDate())
                        .title(improvement.getTitle())
                        .build();
                elasticInquiryRepository.save(inquiryDoc);
                elasticContentRepository.save(c);
                elasticImproverRepository.save(improvementDocument);
            }
            else{
                Board b=createBoard(m, Category.ESPORTS, CategoryType.FREE,"hello",
                        "오늘은 날씨가 매우 맑아서 공원에 나가 산책을 하기로 했다. 새들이 지저귀는 소리를 들으며 길을 걷다 보면 마음이 편안해진다. 친구들과 함께 커피를 마시며 이야기를 나누는 시간도 즐겁다. " +
                                "점심으로는 간단하게 김밥과 떡볶이를 먹고, 오후에는 책을 읽으며 휴식을 취했다. 저녁이 되자 노을이 아름답게 물들어 하루를 마무리했다.");
                ContentDocument c
                        =ContentDocument
                        .builder()
                        .staticDataType(StaticDataType.BOARD)
                        .adminControlType(AdminControlType.HIDDEN)
                        .title("hello")
                        .contentId(b.getId())
                        .content(b.getContent())
                        .createDate(b.getCreateDate())
                        .reportCount(0L)
                        .email(m.getEmail())
                        .build();
                elasticContentRepository.save(c);
            }
        }

    }

    //@Test
    void contentTest(){

        LocalDateTime now=LocalDateTime.now();
        LocalDateTime end=now.plusDays(1L);
        String startTime= DateFormatUtil.formatByDot.format(now);
        String endTime=DateFormatUtil.formatByDot.format(end);

        ContentRequestDto.RequestContentData requestContentData=
                RequestContentData
                        .builder()
                        .staticDataType(StaticDataType.BOARD)
                        .boardSearchType(BoardSearchType.CONTENT)
                        .searchKeyWord("hello")
                        .startTime(startTime)
                        .endTime(endTime)
                        .reported(false)
                        .offset(1)
                        .build();
        List<ResponseContentSearch> responseContentSearches
        =contentSearchRepository.useElasticSearchForUnionQuery(requestContentData);
        Assertions.assertThat(responseContentSearches.size()).isEqualTo(5);



        ImproveRequestDto.RequestImprovementList requestImprovementList
                = ImproveRequestDto.RequestImprovementList.builder()
                .improvementStatus(ImprovementStatus.PENDING)
                .importantStatus(ImportantStatus.NORMAL)
                .endTime(endTime)
                .nickName(m.getNickname())
                .startTime(startTime)
                .offset(1)
                .build();

        List<ImprovementResponseDto.ResponseImprovement> responseImprovements
                =adminImprovementSearchRepo.getImprovementByElasticSearch(requestImprovementList);

        Assertions.assertThat(responseImprovements.size()).isEqualTo(5);

        InquiryRequestDto.RequestInquiryListCond requestInquiryListCond=
                InquiryRequestDto.RequestInquiryListCond.builder()
                        .isMember(true)
                        .offset(1)
                        .startTime(startTime)
                        .endTime(endTime)
                        .email(m.getEmail())
                        .build();

        List<InquiryResponseDto.ResponseInquiryListCond> responseInquiryLists
                =inquirySearchRepo.getInquiryListByContElasticSearch(requestInquiryListCond);
        Assertions.assertThat(responseInquiryLists.size()).isEqualTo(5);


    }
}
