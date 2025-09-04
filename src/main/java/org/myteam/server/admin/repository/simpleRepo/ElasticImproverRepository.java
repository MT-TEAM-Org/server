package org.myteam.server.admin.repository.simpleRepo;

import org.myteam.server.admin.document.ImprovementDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticImproverRepository extends ElasticsearchRepository<ImprovementDocument,Long> {
}
