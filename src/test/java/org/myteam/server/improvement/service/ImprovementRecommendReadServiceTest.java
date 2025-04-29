package org.myteam.server.improvement.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementRecommend;
import org.myteam.server.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class ImprovementRecommendReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private ImprovementRecommendReadService improvementRecommendReadService;

    private Member member;
    private UUID memberPublicId;
    private Improvement improvement;

    @Transactional
    @BeforeEach
    void setUp() {
        member = createMember(1);
        memberPublicId = member.getPublicId();
        improvement = improvementRepository.save(
                Improvement.builder()
                        .member(member)
                        .title("개선 요청 제목")
                        .content("개선 요청 내용")
                        .createdIp("127.0.0.1")
                        .build()
        );
    }

    @Test
    @DisplayName("isRecommended - 추천한 경우 true 반환")
    void isRecommended_true() {
        // given
        improvementRecommendRepository.save(ImprovementRecommend.builder()
                .improvement(improvement)
                .member(member)
                .build());

        // when
        boolean result = improvementRecommendReadService.isRecommended(improvement.getId(), memberPublicId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isRecommended - 추천하지 않은 경우 false 반환")
    void isRecommended_false() {
        // when
        boolean result = improvementRecommendReadService.isRecommended(improvement.getId(), memberPublicId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isAlreadyRecommended - 추천한 경우 true 반환")
    void isAlreadyRecommended_true() {
        // given
        improvementRecommendRepository.save(ImprovementRecommend.builder()
                .improvement(improvement)
                .member(member)
                .build());

        // when
        boolean result = improvementRecommendReadService.isAlreadyRecommended(improvement.getId(), memberPublicId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAlreadyRecommended - 추천하지 않은 경우 예외 발생")
    void isAlreadyRecommended_throwException() {
        // when & then
        assertThatThrownBy(() -> improvementRecommendReadService.isAlreadyRecommended(improvement.getId(), memberPublicId))
                .isInstanceOf(PlayHiveException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NO_MEMBER_RECOMMEND_RECORD);
    }

}