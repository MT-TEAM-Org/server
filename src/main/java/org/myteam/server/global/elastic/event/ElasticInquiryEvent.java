package org.myteam.server.global.elastic.event;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.document.InquiryDocument;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ElasticInquiryEvent {

    private Long id;
    private String content;
    private String nickName;
    private LocalDateTime createDate;
    private String email;
    private Boolean isAdminAnswered;
    private Boolean isMember;

    @Builder
    public ElasticInquiryEvent(Long id, String content, String nickName, LocalDateTime createDate,
                               String email, Boolean isAdminAnswered, Boolean isMember) {
        this.id = id;
        this.content = content;
        this.nickName = nickName;
        this.createDate = createDate;
        this.email = email;
        this.isAdminAnswered = isAdminAnswered;
        this.isMember = isMember;
    }
    public InquiryDocument getDoc(){
        return InquiryDocument.builder()
                .id(this.id)
                .email(this.email)
                .createDate(this.createDate)
                .isAdminAnswered(this.isAdminAnswered)
                .content(this.content)
                .nickName(this.nickName)
                .isMember(this.isMember)
                .build();
    }
}
