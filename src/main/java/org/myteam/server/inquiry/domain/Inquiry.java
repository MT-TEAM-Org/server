package org.myteam.server.inquiry.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inquiry extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "문의 내용이 없으면 안됩니다.")
    private String content;

    @ManyToOne(optional = true)
    @JoinColumn(name = "public_id")
    private Member member;

    private String clientIp;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private boolean isAdminAnswered; // 관리자가 답변했는지

    @OneToOne(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    private InquiryCount inquiryCount;

    @Column(name = "email")
    private String email;

    public boolean isAuthor(Member member) {
        return this.member.equals(member);
    }

    /**
     * 작성자와 일치 하는지 검사 (어드민도 수정/삭제 허용)
     */
    public void verifyInquiryAuthor(Member member) {
        if (!this.isAuthor(member) && !member.isAdmin()) {
            throw new PlayHiveException(ErrorCode.POST_AUTHOR_MISMATCH);
        }
    }

    public void updateAdminAnswered(boolean state) {
        this.isAdminAnswered =state;
    }
}
