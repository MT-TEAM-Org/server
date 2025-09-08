package org.myteam.server.global.elastic.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.document.ContentDocument;
import org.myteam.server.admin.utill.enums.AdminControlType;
import org.myteam.server.admin.utill.enums.StaticDataType;


@Getter
@NoArgsConstructor
public class ElasticContentUpdateEvent {

    private StaticDataType staticDataType;
    private String nickName;
    private AdminControlType adminControlType;
    private Boolean isReported;
    private Long contentId;
    private String title;
    private String content;


    public ElasticContentUpdateEvent(StaticDataType staticDataType, String nickName, AdminControlType adminControlType,
                                     Boolean isReported, Long contentId, String title, String content) {
        this.staticDataType = staticDataType;
        this.nickName = nickName;
        this.adminControlType = adminControlType;
        this.isReported = isReported;
        this.contentId = contentId;
        this.title = title;
        this.content = content;
    }

    @Builder


    public ContentDocument updateDoc(ContentDocument doc){
        return ContentDocument.builder()
                .id(doc.getId())
                .email(doc.getEmail())
                .nickName(this.nickName!=null ? this.nickName:doc.getNickName())
                .staticDataType(doc.getStaticDataType())
                .adminControlType(this.adminControlType!=null ? this.adminControlType:doc.getAdminControlType())
                .isReported(this.isReported!=null ? this.isReported :doc.getIsReported())
                .contentId(doc.getContentId())
                .title(this.title!=null? this.title:doc.getTitle())
                .content(this.content!=null? this.content:doc.getContent())
                .createDate(doc.getCreateDate())
                .build();
    }


}
