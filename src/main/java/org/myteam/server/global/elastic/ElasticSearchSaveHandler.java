package org.myteam.server.global.elastic;


import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.document.ContentDocument;
import org.myteam.server.admin.document.ImprovementDocument;
import org.myteam.server.admin.document.InquiryDocument;
import org.myteam.server.admin.repository.simpleRepo.ElasticContentRepository;
import org.myteam.server.admin.repository.simpleRepo.ElasticImproverRepository;
import org.myteam.server.admin.repository.simpleRepo.ElasticInquiryRepository;
import org.myteam.server.global.elastic.event.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ElasticSearchSaveHandler {

    private final ElasticContentRepository elasticContentRepository;
    private final ElasticImproverRepository elasticImproverRepository;
    private final ElasticInquiryRepository elasticInquiryRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void saveContentAfterCommit(ElasticContentEvent contentEvent){
        elasticContentRepository.save(contentEvent.getDoc());
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void saveInquiryAfterCommit(ElasticInquiryEvent inquiryEvent){
        elasticInquiryRepository.save(inquiryEvent.getDoc());
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void saveImproveAfterCommit(ElasticImproveEvent improveEvent){
        elasticImproverRepository.save(improveEvent.getDoc());
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateContentAfterCommit(ElasticContentUpdateEvent contentEvent){
       ContentDocument contentDocument=
               elasticContentRepository.findByContentIdAndStaticDataType(contentEvent.getContentId(),contentEvent.getStaticDataType()).get();
       ContentDocument toUpdateDoc=contentEvent.updateDoc(contentDocument);
       elasticContentRepository.save(toUpdateDoc);

    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateInquiryAfterCommit(ElasticInquiryUpdateEvent inquiryEvent){
        InquiryDocument inquiryDocument=
                elasticInquiryRepository.findById(inquiryEvent.getId()).get();
        InquiryDocument toUpdateDoc=inquiryEvent.updateDoc(inquiryDocument);
        elasticInquiryRepository.save(toUpdateDoc);
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateImproveAfterCommit(ElasticImproveUpdateEvent improveEvent){
        ImprovementDocument improvementDocument=
                elasticImproverRepository.findById(improveEvent.getId()).get();
        ImprovementDocument toUpdateDoc=improveEvent.updateDoc(improvementDocument);
        elasticImproverRepository.save(toUpdateDoc);
    }


}
