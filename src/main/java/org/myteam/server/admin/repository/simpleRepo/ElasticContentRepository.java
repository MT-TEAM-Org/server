package org.myteam.server.admin.repository.simpleRepo;

import org.myteam.server.admin.document.ContentDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticContentRepository extends ElasticsearchRepository<ContentDocument,String> {
}
