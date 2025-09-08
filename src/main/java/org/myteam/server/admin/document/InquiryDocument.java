package org.myteam.server.admin.document;

import com.esotericsoftware.kryo.serializers.FieldSerializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.inquiry.domain.InquiryCount;
import org.myteam.server.member.entity.Member;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Setting(replicas = 0)
@Document(indexName = "inquirydocument")
public class InquiryDocument {

    @Id
    @Field(type = FieldType.Long,index = false)
    private Long id;

    @Field(type= FieldType.Text)
    private String content;

    @Field(type=FieldType.Text)
    private String nickName;

    @Field(type=FieldType.Date,format= DateFormat.date_hour_minute_second)
    private LocalDateTime createDate;

    @Field(type=FieldType.Text)
    private String email;

    @Field(type=FieldType.Boolean)
    private Boolean isAdminAnswered;

    @Field(type=FieldType.Boolean)
    private Boolean isMember;

    @Builder
    public InquiryDocument(Long id, String content, String nickName,
                           LocalDateTime createDate, String email, Boolean isAdminAnswered, Boolean isMember) {
        this.id = id;
        this.content = content;
        this.nickName = nickName;
        this.createDate = createDate;
        this.email = email;
        this.isAdminAnswered = isAdminAnswered;
        this.isMember = isMember;
    }
}
