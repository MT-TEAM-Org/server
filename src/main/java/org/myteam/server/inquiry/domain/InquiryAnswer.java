package org.myteam.server.inquiry.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "inquiry_id", nullable = false, unique = true)
    private Inquiry inquiry;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime answeredAt;

    public static InquiryAnswer createAnswer(Inquiry inquiry, String content) {
        return InquiryAnswer.builder()
                .inquiry(inquiry)
                .content(content)
                .answeredAt(LocalDateTime.now())
                .build();
    }

    public void updateContent(String content) {
        this.content = content;
        this.answeredAt = LocalDateTime.now();
    }
}
