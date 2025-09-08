package org.myteam.server.admin.document;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.utill.enums.AdminControlType;
import org.myteam.server.admin.utill.enums.StaticDataType;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Setting(replicas = 0)
@Document(indexName = "contentdocument")
public class ContentDocument {
    @Id
    private String id;

    @Field(type=FieldType.Keyword,index = false)
    private String email;

    @Field(type=FieldType.Text)
    private String nickName;

    @Field(type=FieldType.Keyword)
    private StaticDataType staticDataType;

    @Field(type=FieldType.Keyword)
    private AdminControlType adminControlType;

    @Field(type=FieldType.Boolean)
    private Boolean isReported;

    @Field(type=FieldType.Long,index = false)
    private Long contentId;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type=FieldType.Date,format= DateFormat.date_hour_minute_second,index = false)
    private LocalDateTime createDate;


    @Builder
    public ContentDocument(String id,String email, String nickName, StaticDataType staticDataType, AdminControlType adminControlType,
                           Boolean isReported, Long contentId, String title, String content, LocalDateTime createDate) {
        this.id=id;
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


}
