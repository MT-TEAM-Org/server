package org.myteam.server.inquiry.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryCount;
import org.myteam.server.inquiry.dto.response.InquiryResponse.*;
import org.myteam.server.inquiry.dto.request.InquiryRequest.*;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.myteam.server.global.exception.ErrorCode.USER_NOT_FOUND;
import static org.myteam.server.inquiry.domain.InquiryOrderType.NONE;

class InquiryReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private InquiryReadService inquiryReadService;

    private final long NON_EXISTS = 999999L;
    private Member member;
    private Inquiry inquiry;
    private List<Inquiry> inquiryList = new ArrayList<>();

    @Transactional
    @BeforeEach
    void setUp() {
        member = createMember(1);
        inquiry = createInquiry(member);

        for (int i = 1; i <= 5; i++) {
            Inquiry saved = inquiryRepository.save(
                    Inquiry.builder()
                            .member(member)
                            .content("문의 내용 " + i)
                            .clientIp("127.0.0." + i)
                            .createdAt(LocalDateTime.now())
                            .email(null)
                            .build()
            );
            inquiryList.add(saved);
            inquiryCountRepository.save(InquiryCount.createCount(inquiry));
        }

        when(redisCountService.getCommonCount(ServiceType.CHECK, DomainType.INQUIRY, inquiry.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        for (int i = 0; i < inquiryList.size(); i++) {
            when(redisCountService.getCommonCount(ServiceType.CHECK, DomainType.INQUIRY, inquiryList.get(i).getId(), null))
                    .thenReturn(new CommonCountDto(0, 0, 0));
        }
    }

    @Transactional
    @Test
    @DisplayName("1. 내 문의 목록 조회 - 일반 유저")
    void getInquiriesByMember_user() {
        // given
        when(securityReadService.getMember()).thenReturn(member);

        InquiryServiceRequest request = InquiryServiceRequest.builder()
                .page(1)
                .size(5)
                .orderType(NONE)
                .searchType(null)
                .search(null)
                .build();

        // when
        InquiriesListResponse response = inquiryReadService.getInquiriesByMember(request);

        // then
        assertThat(response.getList()).isNotNull();
        assertThat(response.getList().getContent()).hasSize(5);
        assertThat(response.getList().getContent())
                .extracting("content")
                .containsExactlyInAnyOrder("문의 내용 1", "문의 내용 2", "문의 내용 3", "문의 내용 4", "문의 내용 5");
    }

    @Test
    @DisplayName("2. 내 문의 목록 조회 - 관리자")
    void getInquiriesByMember_admin() {
        // given
        Member admin = createAdmin(2);
        when(securityReadService.getMember()).thenReturn(admin);

        InquiryServiceRequest request = InquiryServiceRequest.builder()
                .page(1)
                .size(5)
                .orderType(null)
                .searchType(null)
                .search(null)
                .build();

        // when
        InquiriesListResponse response = inquiryReadService.getInquiriesByMember(request);

        // then
        assertThat(response.getList()).isNotNull();
    }

    @Test
    @DisplayName("3. 내 문의 개수 조회")
    void getInquiriesCountByMember() {
        // given
        UUID publicId = member.getPublicId();

        // when
        int count = inquiryReadService.getInquiriesCountByMember(publicId);

        // then
        assertThat(count).isEqualTo(6); // inquiry 1개 + 추가 5개
    }

    @Transactional
    @Test
    @DisplayName("4. 문의 상세 조회 성공")
    void getInquiryById_success() {
        // given
        Member inquiryMember = inquiryRepository.findById(inquiry.getId()).orElseThrow().getMember();
        when(securityReadService.getMember()).thenReturn(inquiryMember);

        // when
        InquiryDetailsResponse response = inquiryReadService.getInquiryById(inquiry.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo(inquiry.getContent());
    }

    @Test
    @DisplayName("5. 문의 상세 조회 - 작성자가 아닌 경우 예외 발생")
    void getInquiryById_fail_not_author() {
        // given
        Member other = createMember(3);
        when(securityReadService.getMember()).thenReturn(other);

        // when
        PlayHiveException ex = assertThrows(PlayHiveException.class,
                () -> inquiryReadService.getInquiryById(inquiry.getId())
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.POST_AUTHOR_MISMATCH);
    }

    @Test
    @DisplayName("6. 문의 존재 여부 확인 - 존재하는 경우")
    void existsById_true() {
        // when
        boolean exists = inquiryReadService.existsById(inquiry.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("7. 문의 존재 여부 확인 - 존재하지 않는 경우")
    void existsById_false() {
        // when
        boolean exists = inquiryReadService.existsById(NON_EXISTS);

        // then
        assertThat(exists).isFalse();
    }
}