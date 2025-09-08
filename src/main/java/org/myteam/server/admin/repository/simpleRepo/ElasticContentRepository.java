package org.myteam.server.admin.repository.simpleRepo;

import org.myteam.server.admin.document.ContentDocument;
import org.myteam.server.admin.utill.enums.StaticDataType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface ElasticContentRepository extends ElasticsearchRepository<ContentDocument,String> {
    Optional<ContentDocument> findByContentIdAndStaticDataType(Long contentId, StaticDataType staticDataType);
}
