package org.myteam.server.improvement.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementCount;
import org.myteam.server.improvement.dto.request.ImprovementRequest.*;
import org.myteam.server.improvement.dto.response.ImprovementResponse.*;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ImprovementReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private ImprovementReadService improvementReadService;

    @MockBean
    private ImprovementRecommendReadService improvementRecommendReadService;

    private Member member;
    private Improvement improvement;
    private List<Improvement> improvementList = new ArrayList<>();

    @Transactional
    @BeforeEach
    void setUp() {
        member = createMember(1);
        improvement = improvementRepository.save(
                Improvement.builder()
                        .member(member)
                        .title("개선 요청 제목")
                        .content("개선 요청 내용")
                        .createdIp("127.0.0.1")
                        .build()
        );
        improvementCountRepository.save(
                new ImprovementCount(improvement, 0, 0, 0)
        );
        for (int i = 1; i <= 5; i++) {
            improvementList.add(improvementRepository.save(
                    Improvement.builder()
                            .member(member)
                            .title("title" + i)
                            .content("개선 요청 내용" + i)
                            .createdIp("127.0.0.1")
                            .build()
            ));
            improvementCountRepository.save(
                    new ImprovementCount(improvementList.get(i - 1), 0, 0, 0)
            );
        }
    }

    @Transactional
    @Test
    @DisplayName("개선요청 ID로 조회 성공")
    void findById_success() {
        // when
        Improvement result = improvementReadService.findById(improvement.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("개선 요청 제목");
    }

    @Test
    @DisplayName("개선요청 ID로 조회 실패 - 존재하지 않는 경우")
    void findById_notFound() {
        // when & then
        assertThatThrownBy(() -> improvementReadService.findById(9999L))
                .isInstanceOf(PlayHiveException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.IMPROVEMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("개선요청 상세 조회 - 로그인 사용자 있음")
    void getImprovement_loginUser() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(member.getPublicId());
        when(redisCountService.getCommonCount(ServiceType.CHECK, DomainType.IMPROVEMENT, improvement.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        when(improvementRecommendReadService.isRecommended(improvement.getId(), member.getPublicId())).thenReturn(true);

        // when
        ImprovementSaveResponse response = improvementReadService.getImprovement(improvement.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.isRecommended()).isTrue();
    }

    @Test
    @DisplayName("개선요청 상세 조회 - 로그인 사용자 없음")
    void getImprovement_notLoginUser() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(null);
        when(redisCountService.getCommonCount(ServiceType.CHECK, DomainType.IMPROVEMENT, improvement.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));

        // when
        ImprovementSaveResponse response = improvementReadService.getImprovement(improvement.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.isRecommended()).isFalse();
    }

    @Transactional
    @Test
    @DisplayName("개선요청 목록 조회 성공")
    void getImprovementList_success() {
        // given
        when(redisCountService.getCommonCount(eq(ServiceType.CHECK), eq(DomainType.IMPROVEMENT), any(), isNull()))
                .thenReturn(new CommonCountDto(0, 0, 0)); // 🔥 getHotImprovementIdList()용

        for (int i = 0; i < improvementList.size(); i++) {
            when(redisCountService.getCommonCount(ServiceType.CHECK, DomainType.IMPROVEMENT, improvementList.get(i).getId(), null))
                    .thenReturn(new CommonCountDto(0, 0, 0)); // 🔥 improvement 조회용
        }
        ImprovementServiceRequest request = ImprovementServiceRequest.builder()
                .page(1)
                .size(10)
                .orderType(null)
                .searchType(null)
                .search(null)
                .build();


        // when
        ImprovementListResponse response = improvementReadService.getImprovementList(request);

        // then
        assertThat(response.getList()).isNotNull();
        assertThat(response.getList().getContent()).hasSize(6);
        assertThat(response.getList().getContent())
                .extracting("title")
                .containsExactlyInAnyOrder("title1", "title2", "title3", "title4", "title5", "개선 요청 제목");
    }

    @Test
    @DisplayName("개선요청 ID 존재 여부 확인")
    void existsById_success() {
        // when
        boolean exists = improvementReadService.existsById(improvement.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("개선요청 ID 존재하지 않는 경우")
    void existsById_notFound() {
        // when
        boolean exists = improvementReadService.existsById(9999L);

        // then
        assertThat(exists).isFalse();
    }
}