package org.myteam.server.admin.repository;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.document.ContentDocument;
import org.myteam.server.admin.dto.request.ContentRequestDto;
import org.myteam.server.admin.dto.response.ResponseContentDto;
import org.myteam.server.admin.utill.enums.DateFormatEnum;
import org.myteam.server.admin.utill.enums.StaticDataType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.report.domain.ReportType;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static org.myteam.server.admin.dto.response.ResponseContentDto.*;
import static org.myteam.server.report.domain.QReport.report;

@RequiredArgsConstructor
@Repository
public class ContentElasticRepository {

    private final ElasticsearchOperations elasticsearchOperations;
    private final MemberReadService memberReadService;
    private final BlazeJPAQueryFactory blazeJPAQueryFactory;

    public Page<ResponseContentSearch> useElasticSearchForUnionQuery(ContentRequestDto.RequestContentData requestContentData){
        List<Query> mustQuery=makeMustQuery(requestContentData);
        List<Query> filterQuery=makeFilterQuery(requestContentData);
        PageRequest pageRequest=PageRequest.of(requestContentData.getOffset(),10);
        Query query=new BoolQuery.Builder()
                .filter(filterQuery)
                .must(mustQuery)
                .build()
                ._toQuery();
        NativeQuery nativeQuery=NativeQuery.builder()
                .withQuery(query)
                .withSort(List.of(SortOptions.of(s->s.field(f->f.field("createDate")
                        .order(SortOrder.Desc)))))
                .withPageable(pageRequest)
                .build();


        SearchHits<ContentDocument> datas= elasticsearchOperations.search(nativeQuery, ContentDocument.class);
        List<ResponseContentSearch> responseContentSearches=datas.stream().map(x->{
            return mappingDocToDto(x.getContent());
        }).collect(Collectors.toList());

        DateFormatUtil.makeElasticTimeByFormatter(responseContentSearches, DateFormatEnum.formatByDotReq);
        return new PageImpl<>(responseContentSearches,pageRequest,datas.getTotalHits());
    }



    private List<Query> makeFilterQuery(ContentRequestDto.RequestContentData requestContentData){
        List<Query> filterQuery=new ArrayList<>();
        if(requestContentData.getAdminControlType()!=null){
            TermQuery termQuery=TermQuery.of(t->t.field("adminControlType")
                    .value(requestContentData.getAdminControlType().name()));
            filterQuery.add(termQuery._toQuery());
        }
        if(requestContentData.provideEndTime()!=null && requestContentData.provideEndTime()!=null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            RangeQuery rangeQuery=new RangeQuery.Builder()
                    .date(v->v.field("createDate")
                            .gte(requestContentData.provideStartTime().format(formatter))
                            .lte(requestContentData.provideEndTime().format(formatter)))
                    .build();
            filterQuery.add(rangeQuery._toQuery());
        }
        if(requestContentData.getIsReported()!=null){
            TermQuery termQuery=TermQuery.of(t->t.field("isReported")
                    .value(requestContentData.getIsReported()));
            filterQuery.add(termQuery._toQuery());
        }
        if(requestContentData.getStaticDataType()!=null){
            TermQuery termQuery=TermQuery.of(t->t.field("staticDataType")
                    .value(requestContentData.getStaticDataType().name()));
            filterQuery.add(termQuery._toQuery());
        }

        return filterQuery;
    }
    private List<Query> makeMustQuery(ContentRequestDto.RequestContentData requestContentData){
        List<Query> mustQuery=new ArrayList<>();
        BoardSearchType boardSearchType=requestContentData.getBoardSearchType();
        String searchKeyWord=requestContentData.getSearchKeyWord();
        if(boardSearchType!=null){
            if(boardSearchType==BoardSearchType.CONTENT||boardSearchType==BoardSearchType.COMMENT){
                MatchQuery matchQuery=MatchQuery.of(m->
                        m.field("content")
                                .query(searchKeyWord));
                mustQuery.add(matchQuery._toQuery());
            }
            if(boardSearchType==BoardSearchType.TITLE_CONTENT){
                MultiMatchQuery multiMatchQuery=MultiMatchQuery.of(
                        m->m.fields(List.of("content","title"))
                                .query(searchKeyWord)
                );
                mustQuery.add(multiMatchQuery._toQuery());
            }
            if(boardSearchType==BoardSearchType.TITLE){
                MatchQuery matchQuery=MatchQuery.of(m->
                        m.field("title")
                                .query(searchKeyWord));
                mustQuery.add(matchQuery._toQuery());
            }
            if(requestContentData.getBoardSearchType().equals(BoardSearchType.NICKNAME)){
                MatchQuery matchQuery=MatchQuery.of(m->m.field("nickName")
                        .query(requestContentData.getSearchKeyWord())
                        .fuzziness("AUTO"));
                mustQuery.add(matchQuery._toQuery());
            }
        }
        return mustQuery;
    }


    private ResponseContentSearch mappingDocToDto(ContentDocument contentDocument){
        Member member1=memberReadService.findByEmail(contentDocument.getEmail());
        String status=member1.getStatus().equals(MemberStatus.ACTIVE) ? "정상" :
                member1.getStatus().equals(MemberStatus.WARNED) ? "경고" :
                        member1.getStatus().equals(MemberStatus.PENDING) ? "대기중":"정지";
        String reported=contentDocument.getIsReported() ? "신고" :"미신고";

        String staticDataType=contentDocument.getStaticDataType().equals(StaticDataType.BOARD) ? "게시글":
                contentDocument.getStaticDataType().equals(StaticDataType.COMMENT) ? "댓글"
                        : "채팅";
        Long reportNum=0L;
        if(contentDocument.getIsReported()!=null&&contentDocument.getIsReported()) {
            if (contentDocument.getStaticDataType().equals(StaticDataType.COMMENT)) {
                reportNum = Optional.ofNullable(blazeJPAQueryFactory.select(report.count())
                                .from(report)
                                .where(report.reportedContentId.eq(contentDocument.getContentId())
                                        .and(report.reportType.eq(ReportType.COMMENT)))
                                .fetchOne())
                        .orElse(0L);
            }
            if (contentDocument.getStaticDataType().equals(StaticDataType.BOARD)) {
                reportNum = Optional.ofNullable(blazeJPAQueryFactory.select(report.count())
                                .from(report)
                                .where(report.reportedContentId.eq(contentDocument.getContentId())
                                        .and(report.reportType.eq(ReportType.BOARD)))
                                .fetchOne())
                        .orElse(0L);
            }
        }
        ResponseContentSearch responseContentSearch=new ResponseContentSearch(
                contentDocument.getContentId(),member1.getNickname(),staticDataType,
                contentDocument.getContent(),contentDocument.getCreateDate().toString(),status,
                contentDocument.getAdminControlType().name(),reportNum,reported
        );
        return responseContentSearch;
    }
}
