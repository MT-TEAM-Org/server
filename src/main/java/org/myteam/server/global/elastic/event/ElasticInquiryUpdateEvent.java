package org.myteam.server.global.elastic.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.document.InquiryDocument;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ElasticInquiryUpdateEvent {

    private Long id;
    private String content;
    private String nickName;
    private Boolean isAdminAnswered;
    private Boolean isMember;


    @Builder
    public ElasticInquiryUpdateEvent(Long id, String content, String nickName,
                                     Boolean isAdminAnswered, Boolean isMember) {
        this.id = id;
        this.content = content;
        this.nickName = nickName;
        this.isAdminAnswered = isAdminAnswered;
        this.isMember = isMember;
    }



    public InquiryDocument updateDoc(InquiryDocument doc){
        return InquiryDocument.builder()
                .id(doc.getId())
                .email(doc.getEmail())
                .createDate(doc.getCreateDate())
                .isAdminAnswered(this.isAdminAnswered!=null ? this.isAdminAnswered :doc.getIsAdminAnswered())
                .content(this.content!=null ? this.content : doc.getContent())
                .nickName(this.nickName!=null ? this.nickName : doc.getNickName())
                .isMember(this.isMember!=null? this.isMember:doc.getIsMember())
                .build();
    }
}
