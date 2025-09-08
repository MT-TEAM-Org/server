package org.myteam.server.admin.repository;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.document.ImprovementDocument;
import org.myteam.server.admin.dto.request.ImproveRequestDto;
import org.myteam.server.admin.dto.response.ImprovementResponseDto;
import org.myteam.server.admin.utill.enums.DateFormatEnum;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.myteam.server.improvement.domain.ImportantStatus;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.myteam.server.admin.dto.response.ImprovementResponseDto.*;

@RequiredArgsConstructor
@Repository
public class ImproveElasticRepository {



    private final ElasticsearchOperations elasticsearchOperations;

    public Page<ResponseImprovement> getImprovementByElasticSearch(ImproveRequestDto.RequestImprovementList requestImprovementList){
        List<Query> must=makeMustQuery(requestImprovementList);
        List<Query> filter=filterQuery(requestImprovementList);
        PageRequest pageRequest=PageRequest.of(requestImprovementList.getOffset(),10);

        Query query=new BoolQuery.Builder()
                .filter(filter)
                .must(must)
                .build()
                ._toQuery();

        NativeQuery nativeQuery=NativeQuery.builder()
                .withQuery(query)
                .withSort(List.of(SortOptions.of(s->s.field(f->f.field("createDate")
                        .order(SortOrder.Desc)))))
                .withPageable(pageRequest)
                .build();
        SearchHits<ImprovementDocument> data=elasticsearchOperations.search(nativeQuery, ImprovementDocument.class);

        List<ResponseImprovement> improvements=data.stream().map(x->{
            ImprovementDocument doc=x.getContent();
            String importantStatus= doc.getImportantStatus().equals(ImportantStatus.LOW) ? "낮음"
                    :doc.getImportantStatus().equals(ImportantStatus.HIGH) ? "높음 ":"중간";
            String improveStatus=doc.getImprovementStatus().equals(ImprovementStatus.PENDING) ?"접수전"
                    :doc.getImprovementStatus().equals(ImprovementStatus.RECEIVED) ? "접수완료": "개선완료";
            return new ResponseImprovement(
                    doc.getId(), doc.getMemberId(),importantStatus,improveStatus,0
                    ,doc.getNickName(),doc.getTitle(),
                    doc.getContent(),doc.getCreateDate().toString()
            );
        }).collect(Collectors.toList());
        DateFormatUtil.makeElasticTimeByFormatter(improvements, DateFormatEnum.formatByDotReq);
        return new PageImpl<>(improvements,pageRequest,data.getTotalHits());
    }

    private List<Query> filterQuery(ImproveRequestDto.RequestImprovementList requestImprovementList){
        List<Query> filter=new ArrayList<>();
        if(requestImprovementList.getImportantStatus()!=null){
            TermQuery termQuery=TermQuery.of(t->t.field("importantStatus")
                    .value(requestImprovementList.getImportantStatus().name()));
            filter.add(termQuery._toQuery());
        }
        if(requestImprovementList.getImprovementStatus()!=null){
            TermQuery termQuery=TermQuery.of(t->t.field("improvementStatus")
                    .value(requestImprovementList.getImprovementStatus().name()));
            filter.add(termQuery._toQuery());
        }
        if(requestImprovementList.provideEndTime()!=null&&requestImprovementList.provideStartTime()!=null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            RangeQuery rangeQuery=RangeQuery.of(r->r.date(
                    v->v.field("createDate")
                            .gte(requestImprovementList.provideStartTime().format(formatter))
                            .lt(requestImprovementList.provideEndTime().format(formatter))
            ));
            filter.add(rangeQuery._toQuery());
        }
        return filter;
    }

    private List<Query> makeMustQuery(ImproveRequestDto.RequestImprovementList requestImprovementList){
        List<Query> mustQuery=new ArrayList<>();

        if(requestImprovementList.getContent()!=null){
            MatchQuery matchQuery=MatchQuery.of(m->m.field("content")
                    .query(requestImprovementList.getContent()));
            mustQuery.add(matchQuery._toQuery());
        }
        if(requestImprovementList.getTitle()!=null){
            MatchQuery matchQuery=MatchQuery.of(m->m.field("title")
                    .query(requestImprovementList.getTitle()));
            mustQuery.add(matchQuery._toQuery());
        }
        if(requestImprovementList.getNickName()!=null){
            MatchQuery matchQuery=MatchQuery.of(m->m.field("nickName")
                    .query(requestImprovementList.getNickName())
                    .fuzziness("AUTO"));
            mustQuery.add(matchQuery._toQuery());
        }

        return mustQuery;
    }
}
