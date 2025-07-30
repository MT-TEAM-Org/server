package org.myteam.server.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.utill.AdminControlType;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.improvement.domain.ImportantStatus;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.member.domain.MemberStatus;

import java.util.UUID;

public record AdminMemoRequestDto() {

    @Getter
    @NoArgsConstructor
    @Schema(description = "회원에 대한 메모 작성을 요청시에 쓰이는 값입니다")
    public static class AdminMemoMemberRequest{
        @NotNull(message = "memberId는 비어있으면 안됩니다")
        private UUID memberId;
        @NotNull(message = "회원 상태값이 비어있습니다.")
        @Schema(description = "수정이 없다면 기존값을 수정했다면 수정값을 주세요", example = "INACTIVE, ACTIVE, PENDING,WARNED")
        private MemberStatus memberStatus;
        @Schema(description = "내용이 없다면 null로 주세요")
        private String content;
        @Builder
        public AdminMemoMemberRequest(UUID memberId,MemberStatus memberStatus, String content){
            this.memberId=memberId;
            this.memberStatus=memberStatus;
            this.content = content;
        }

    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "개선 요청 메모 작성 요청시 쓰이는 값입니다.")
    public static class AdminMemoImprovementRequest {
        @NotNull(message = "contentid는 비어있으면 안됩니다")
        private Long contentId;
        @NotNull(message = "개선 진행도값이 비어있습니다.")
        @Schema(description = "수정이 없다면 기존값을 수정했다면 수정값을 주세요", example = "PENDING RECEIVED COMPLETED")
        private ImprovementStatus improvementStatus;
        @NotNull(message = "개선 중요도값이 비어있습니다.")
        @Schema(description = "수정이 없다면 기존값을 수정했다면 수정값을 주세요", example = "LOW NORMAL HIGH")
        private ImportantStatus importantStatus;
        @Schema(description = "내용이 없다면 null로 주세요")
        private String content;
        @Schema(description ="생성시 로그 남기기 여부를 결정하는 값입니다. 무시하셔도됩니다." )
        private String auto;
        @Builder
        public AdminMemoImprovementRequest(Long contentId, String content
                , ImprovementStatus improvementStatus, ImportantStatus importantStatus,String auto) {
            this.contentId = contentId;
            this.importantStatus = importantStatus;
            this.improvementStatus = improvementStatus;
            this.content = content;
            this.auto=auto;
        }
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "문의 메모 작성 요청시 쓰이는 값입니다.")
    public static class AdminMemoInquiryRequest {
        @NotNull(message = "contentid는 비어있으면 안됩니다.")
        @Schema(description = "필수값 입니다.")
        private Long contentId;
        @Schema(description = "필수값. 공백이면안됨.")
        @NotBlank(message = "문의는 빈값이 즉 공백이아닙니다.")
        @NotNull(message = "문의메모는 답변이 존재해야만합니다.")
        private String content;
        @NotNull(message ="이메일은 비어있으면 안됩니다.")
        @Schema(description = "문의 작성자의 이메일 입니다.")
        private String email;
        @Schema(description = "문의 생성시 로그 남기기용입니다 무시해주세요.")
        private Boolean isMember;

        @Builder
        public AdminMemoInquiryRequest(Long contentId
                , String content, String email,String auto,Boolean isMember) {
            this.contentId = contentId;
            this.content = content;
            this.email = email;
            this.isMember=isMember;
        }
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "댓글 게시글 메모 작성 요청시 쓰이는 값입니다.")
    public static class AdminMemoContentRequest {
        @NotNull(message = "contentid는 비어있으면 안됩니다.")
        @Schema(description = "contentid는 비어있으면 안됩닏.")
        private Long contentId;
        @NotNull(message = "데이터 타입이 비어있습니다.")
        @Schema(description = "댓글이면 COMMENT 게시글이면 BOARD")
        private StaticDataType staticDataType;
        @NotNull(message = "관리자 조정값은 비어있으면 안됩니다.")
        @Schema(description = "수정이 없다면 기존값을 수정했다면 수정값을 주세요", example = "SHOW,PENDING,HIDDEN")
        private AdminControlType adminControlType;
        @Schema(description = "내용이 없다면 null로 주세요")
        private String content;
        @Schema(description ="자동 숨김 처리 여부를 결정하는 값입니다. 무시하셔도됩니다." )
        private String auto;

        @Builder
        public AdminMemoContentRequest(Long contentId, StaticDataType staticDataType, String content
                , AdminControlType adminControlType,String auto) {
            this.contentId = contentId;
            this.staticDataType = staticDataType;
            this.adminControlType = adminControlType;
            this.content = content;
            this.auto=auto;
        }
    }
}
