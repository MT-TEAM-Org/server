package org.myteam.server.global.elastic.event;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.document.ContentDocument;
import org.myteam.server.admin.utill.enums.AdminControlType;
import org.myteam.server.admin.utill.enums.StaticDataType;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ElasticContentEvent {

    private String email;
    private String nickName;
    private StaticDataType staticDataType;
    private AdminControlType adminControlType;
    private Boolean isReported;
    private Long contentId;
    private String title;
    private String content;
    private LocalDateTime createDate;


    @Builder
    public ElasticContentEvent(String email, String nickName, StaticDataType staticDataType,
                               AdminControlType adminControlType, Boolean isReported, Long contentId,
                               String title, String content, LocalDateTime createDate) {
        this.email = email;
        this.nickName = nickName;
        this.staticDataType = staticDataType;
        this.adminControlType = adminControlType;
        this.isReported=isReported;
        this.contentId = contentId;
        this.title = title;
        this.content = content;
        this.createDate = createDate;
    }

    public ContentDocument getDoc(){
        return ContentDocument.builder()
                .email(this.email)
                .nickName(this.nickName)
                .staticDataType(this.staticDataType)
                .adminControlType(this.adminControlType)
                .isReported(this.isReported)
                .contentId(this.contentId)
                .title(this.title)
                .content(this.content)
                .createDate(this.createDate)
                .build();
    }
}
