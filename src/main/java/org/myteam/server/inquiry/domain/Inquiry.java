package org.myteam.server.inquiry.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.myteam.server.member.entity.Member;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
=======
    @NotNull(message = "문의 내용이 없으면 안됩니다.")
>>>>>>> 624ff52 (feat: 문의하기 기능 추가)
    private String content;

    @ManyToOne(optional = true, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "member_id")
    private Member member;

    private String clientIp;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
