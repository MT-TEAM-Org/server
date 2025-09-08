package org.myteam.server.admin.repository;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.document.InquiryDocument;
import org.myteam.server.admin.utill.enums.DateFormatEnum;
import org.myteam.server.global.util.date.DateFormatUtil;
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
import static org.myteam.server.admin.dto.request.InquiryRequestDto.*;
import static org.myteam.server.admin.dto.response.InquiryResponseDto.*;

@Repository
@RequiredArgsConstructor
public class InquiryElasticRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    public Page<ResponseInquiryListCond> getInquiryListByContElasticSearch(RequestInquiryListCond requestInquiryListCond){
        List<Query> filter=makeFilterQuery(requestInquiryListCond);
        List<Query> must=makeMustQuery(requestInquiryListCond);
        PageRequest pageRequest=PageRequest.of(requestInquiryListCond.getOffset(),10);

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
        SearchHits<InquiryDocument> data=elasticsearchOperations.search(nativeQuery, InquiryDocument.class);

        List<ResponseInquiryListCond> responseInquiryListConds=data.stream().map(x->{
            InquiryDocument doc=x.getContent();
            String isAnswered= doc.getIsAdminAnswered() ? "답변완료" :"답변대기";
            String isMember=doc.getIsMember() ? "회원":"비회원";
            String nicknameEmail=doc.getNickName()==null ? doc.getEmail() : doc.getNickName();

            return new ResponseInquiryListCond(
                    doc.getId(),isAnswered,isMember,nicknameEmail,doc.getContent(),doc.getEmail(),
                    doc.getCreateDate().toString()
            );
        }).collect(Collectors.toList());

        DateFormatUtil.makeElasticTimeByFormatter(responseInquiryListConds, DateFormatEnum.formatByDotReq);

        return new PageImpl<>(responseInquiryListConds,pageRequest,data.getTotalHits());

    }

    private List<Query> makeFilterQuery(RequestInquiryListCond requestInquiryListCond){
        List<Query> filterQuery=new ArrayList<>();
        if(requestInquiryListCond.getIsMember()!=null){
            TermQuery termQuery=TermQuery.of(t->t.field("isMember")
                    .value(requestInquiryListCond.getIsMember()));
            filterQuery.add(termQuery._toQuery());
        }
        if(requestInquiryListCond.getIsAnswered()!=null){
            TermQuery termQuery=TermQuery.of(t->t.field("isAdminAnswered")
                    .value(requestInquiryListCond.getIsAnswered()));
            filterQuery.add(termQuery._toQuery());
        }
        if(requestInquiryListCond.provideStartTime()!=null&&requestInquiryListCond.provideEndTime()!=null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            RangeQuery rangeQuery=RangeQuery.of(r->r.date(
                    v->v.field("createDate")
                            .gte(requestInquiryListCond.provideStartTime().format(formatter))
                            .lt(requestInquiryListCond.provideEndTime().format(formatter))
            ));
            filterQuery.add(rangeQuery._toQuery());
        }
        if(requestInquiryListCond.getEmail()!=null){
            MatchQuery matchQuery=MatchQuery.of(m->m.field("email")
                    .query(requestInquiryListCond.getEmail())
                    .fuzziness("AUTO"));
            filterQuery.add(matchQuery._toQuery());
        }

        return filterQuery;
    }
    private List<Query> makeMustQuery(RequestInquiryListCond requestInquiryListCond){
        List<Query> mustQuery=new ArrayList<>();

        if(requestInquiryListCond.getContent()!=null){
            MatchQuery matchQuery=MatchQuery.of(m->m.field("content")
                    .query(requestInquiryListCond.getContent()));
            mustQuery.add(matchQuery._toQuery());
        }

        if(requestInquiryListCond.getNickName()!=null){
            MatchQuery matchQuery=MatchQuery.of(m->m.field("nickName")
                    .query(requestInquiryListCond.getNickName())
                    .fuzziness("AUTO"));
            mustQuery.add(matchQuery._toQuery());
        }
        return mustQuery;
    }
}
