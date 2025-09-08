package org.myteam.server.admin.repository.simpleRepo;

import org.myteam.server.admin.document.InquiryDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticInquiryRepository extends ElasticsearchRepository<InquiryDocument,Long> {
}
