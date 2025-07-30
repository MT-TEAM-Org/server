package org.myteam.server.inquiry.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.admin.entity.AdminContentMemo;
import org.myteam.server.admin.entity.AdminImproveChangeLog;
import org.myteam.server.admin.entity.AdminInquiryChangeLog;
import org.myteam.server.admin.repository.simpleRepo.AdminImproveChangeLogRepo;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InquiryServiceTest extends IntegrationTestSupport {

    @Autowired
    private InquiryService inquiryService;
    private Member member;
    private Inquiry inquiry;

    @Transactional
    @BeforeEach
    void setUp() {
        member = createMember(1);
        inquiry = createInquiry(member);
    }

    @Test
    @DisplayName("로그인 사용자가 문의를 등록하면 성공한다")
    void createInquiry_withLoginUser_success() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(member.getPublicId());
        when(mockMemberReadService.getAdminBot()).thenReturn(member);
        // when
        String content = inquiryService.createInquiry("문의 내용입니다.", null, "127.0.0.1");
        List<AdminInquiryChangeLog> adminImproveChangeLogList=adminInquiryChangeLogRepo.findAll();
        List<AdminContentMemo> adminContentMemos=adminContentMemoRepo.findAll();
        // then
        assertThat(content).isEqualTo("문의 내용입니다.");
        assertThat(adminImproveChangeLogList.size()).isEqualTo(1);
        assertThat(adminContentMemos.size()).isEqualTo(0);
        assertThat(adminImproveChangeLogList.get(0).isMember()).isTrue();
        verify(slackService, times(1)).sendSlackNotification(anyString());
    }

    @Test
    @DisplayName("비로그인 사용자가 이메일을 입력하고 문의를 등록하면 성공한다")
    void createInquiry_withoutLoginUser_withEmail_success() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(null);
        when(mockMemberReadService.getAdminBot()).thenReturn(member);

        // when
        String content = inquiryService.createInquiry("비회원 문의입니다.", "nonmember@example.com", "127.0.0.1");
        List<AdminInquiryChangeLog> adminImproveChangeLogList=adminInquiryChangeLogRepo.findAll();
        List<AdminContentMemo> adminContentMemos=adminContentMemoRepo.findAll();
        // then
        assertThat(content).isEqualTo("비회원 문의입니다.");
        assertThat(adminImproveChangeLogList.size()).isEqualTo(1);
        assertThat(adminContentMemos.size()).isEqualTo(0);
        assertThat(adminImproveChangeLogList.get(0).isMember()).isFalse();
        verify(slackService, times(1)).sendSlackNotification(anyString());
    }

    @Test
    @DisplayName("비로그인 사용자가 이메일 없이 문의 등록하면 예외가 발생한다")
    void createInquiry_withoutLoginUser_noEmail_throws() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(null);

        // when
        PlayHiveException exception = assertThrows(PlayHiveException.class, () ->
                inquiryService.createInquiry("비회원 문의", null, "127.0.0.1")
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INQUIRY_EMAIL_EMPTY);
    }

    @Transactional
    @Test
    @DisplayName("본인이 작성한 문의를 삭제하면 성공한다")
    void deleteInquiry_success() {
        // given
        Member inquiryMember = inquiryRepository.findById(inquiry.getId()).orElseThrow().getMember();
        when(securityReadService.getMember()).thenReturn(inquiryMember);

        // when
        assertThatCode(() ->
                inquiryService.deleteInquiry(inquiry.getId())
        ).doesNotThrowAnyException();

        // then
        assertThat(inquiryRepository.findById(inquiry.getId())).isEmpty();
    }

    @Test
    @DisplayName("다른 사용자가 작성한 문의를 삭제하려고 하면 예외가 발생한다")
    void deleteInquiry_unauthorizedUser_throws() {
        // given
        Member outsider = createMember(2);
        when(securityReadService.getMember()).thenReturn(outsider);

        // when
        PlayHiveException exception = assertThrows(PlayHiveException.class, () ->
                inquiryService.deleteInquiry(inquiry.getId())
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POST_AUTHOR_MISMATCH);
    }
}