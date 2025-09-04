package org.myteam.server.admin.document;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.improvement.domain.ImportantStatus;
import org.myteam.server.improvement.domain.ImprovementCount;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.member.entity.Member;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.myteam.server.improvement.domain.ImprovementStatus.PENDING;

@Getter
@NoArgsConstructor
@Setting(replicas = 0)
@Document(indexName = "improvedocument")
public class ImprovementDocument {


    @Id
    @Field(type = FieldType.Long,index = false)
    public Long id;

    @Field(type = FieldType.Keyword,index = false)
    private UUID memberId;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Text)
    private String nickName;

    @Field(type = FieldType.Keyword)
    private ImprovementStatus improvementStatus;

    @Field(type = FieldType.Keyword)
    private ImportantStatus importantStatus;

    @Field(type=FieldType.Date,format= DateFormat.date_hour_minute_second)
    private LocalDateTime createDate;


    @Builder
    public ImprovementDocument(Long id, UUID memberId, String title, String content, String nickName,
                               ImprovementStatus improvementStatus, ImportantStatus importantStatus,
                               LocalDateTime createDate) {
        this.id = id;
        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.nickName = nickName;
        this.improvementStatus = improvementStatus;
        this.importantStatus = importantStatus;
        this.createDate = createDate;
    }
}
