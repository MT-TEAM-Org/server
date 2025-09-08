package org.myteam.server.global.elastic.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.document.ImprovementDocument;
import org.myteam.server.improvement.domain.ImportantStatus;
import org.myteam.server.improvement.domain.ImprovementStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ElasticImproveUpdateEvent {

    private Long id;
    private String title;
    private String content;
    private String nickName;
    private ImprovementStatus improvementStatus;
    private ImportantStatus importantStatus;

    public ElasticImproveUpdateEvent(Long id, String title, String content, String nickName,
                                     ImprovementStatus improvementStatus, ImportantStatus importantStatus) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.nickName = nickName;
        this.improvementStatus = improvementStatus;
        this.importantStatus = importantStatus;
    }

    @Builder

    public ImprovementDocument updateDoc(ImprovementDocument doc){
        return ImprovementDocument.builder()
                .id(doc.getId())
                .content(this.content!=null ? this.content:doc.getContent())
                .nickName(this.nickName!=null? this.nickName: doc.getNickName())
                .title(this.title!=null? this.title:doc.getTitle())
                .improvementStatus(this.improvementStatus!=null? this.improvementStatus:doc.getImprovementStatus())
                .importantStatus(this.importantStatus!=null? this.importantStatus:doc.getImportantStatus())
                .memberId(doc.getMemberId())
                .createDate(doc.getCreateDate())
                .build();
    }
}
