package org.myteam.server.global.elastic.event;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.document.ImprovementDocument;
import org.myteam.server.improvement.domain.ImportantStatus;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ElasticImproveEvent {


    public Long id;
    private UUID memberId;
    private String title;
    private String content;
    private String nickName;
    private ImprovementStatus improvementStatus;
    private ImportantStatus importantStatus;
    private LocalDateTime createDate;
    @Builder
    public ElasticImproveEvent(Long id, UUID memberId, String title, String content, String nickName,
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
    public ImprovementDocument getDoc(){
        return ImprovementDocument.builder()
                .id(this.id)
                .content(this.content)
                .nickName(this.nickName)
                .title(this.title)
                .improvementStatus(this.improvementStatus)
                .importantStatus(this.importantStatus)
                .memberId(this.memberId)
                .createDate(this.createDate)
                .build();
    }
}
