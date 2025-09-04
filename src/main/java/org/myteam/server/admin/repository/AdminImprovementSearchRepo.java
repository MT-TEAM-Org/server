package org.myteam.server.admin.repository;


import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.document.ImprovementDocument;
import org.myteam.server.admin.document.InquiryDocument;
import org.myteam.server.admin.dto.request.InquiryRequestDto;
import org.myteam.server.admin.dto.response.InquiryResponseDto;
import org.myteam.server.admin.utill.CreateAdminMemo;
import org.myteam.server.admin.utill.enums.DateFormatEnum;
import org.myteam.server.admin.utill.enums.StaticDataType;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.myteam.server.improvement.domain.ImportantStatus;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.improvement.dto.response.ImprovementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.myteam.server.admin.dto.request.AdminMemoRequestDto.AdminMemoImprovementRequest;
import static org.myteam.server.admin.dto.response.ImprovementResponseDto.*;
import static org.myteam.server.admin.dto.request.ImproveRequestDto.*;
import static org.myteam.server.improvement.domain.QImprovement.improvement;
import static org.myteam.server.improvement.domain.QImprovementCount.improvementCount;
import static org.myteam.server.member.entity.QMember.member;


@Repository
@RequiredArgsConstructor
@Transactional
public class AdminImprovementSearchRepo {

    private final JPAQueryFactory queryFactory;
    private final CreateAdminMemo createAdminMemo;
    private final ElasticsearchOperations elasticsearchOperations;

    public void createAdminMemo(AdminMemoImprovementRequest adminMemoRequest) {
        createAdminMemo.createImprovementMemo(adminMemoRequest, queryFactory);
    }

    public Page<ResponseImprovement> getImprovementList(RequestImprovementList requestImprovementList) {
        Pageable pageable = PageRequest.of(requestImprovementList.getOffset(), 10);
        List<ResponseImprovement> responseImprovementList = queryFactory
                .select(
                        Projections.constructor(ResponseImprovement.class,
                                improvement.id,
                                member.publicId,
                                new CaseBuilder()
                                        .when(improvement.importantStatus.eq(ImportantStatus.HIGH))
                                        .then("높음")
                                        .when(improvement.importantStatus.eq(ImportantStatus.NORMAL))
                                        .then("중간")
                                        .otherwise("낮음"),
                                new CaseBuilder()
                                        .when(improvement.improvementStatus.eq(ImprovementStatus.RECEIVED))
                                        .then("접수")
                                        .when(improvement.improvementStatus.eq(ImprovementStatus.PENDING))
                                        .then("대기")
                                        .otherwise("완료"),
                                improvementCount.recommendCount,
                                member.nickname,
                                improvement.title,
                                improvement.content.substring(0, 20),
                                improvement.createDate.stringValue()
                        ))
                .from(improvement)
                .join(member)
                .on(member.eq(improvement.member))
                .join(improvementCount)
                .on(improvementCount.improvement.eq(improvement))
                .where(searchByTimeLine(requestImprovementList.provideStartTime()
                                , requestImprovementList.provideEndTime())
                        , searchByWriter(requestImprovementList.getNickName())
                        , contentSearchCond(requestImprovementList.getContent()),
                        titleSearchCond(requestImprovementList.getTitle()),
                        processStatusCond(requestImprovementList.getImprovementStatus()),
                        searchByImportantStatus(requestImprovementList.getImportantStatus()))
                .orderBy(improvement.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        DateFormatUtil.makeTimeByFormatter(responseImprovementList,DateFormatEnum.formatByDotReq);

        Long count = Optional.ofNullable(queryFactory.select(improvement.count())
                .from(improvement)
                .join(member)
                .on(member.eq(improvement.member))
                .where(searchByTimeLine(requestImprovementList.provideStartTime(), requestImprovementList.provideEndTime())
                        , searchByWriter(requestImprovementList.getNickName())
                        , contentSearchCond(requestImprovementList.getContent()),
                        titleSearchCond(requestImprovementList.getTitle()),
                        processStatusCond(requestImprovementList.getImprovementStatus()),
                        searchByImportantStatus(requestImprovementList.getImportantStatus()))
                .fetchOne()).orElse(0L);
        return new PageImpl<>(responseImprovementList, pageable, count);
    }


    public List<ResponseImprovement> getImprovementByElasticSearch(RequestImprovementList requestImprovementList){
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
        DateFormatUtil.makeElasticTimeByFormatter(improvements,DateFormatEnum.formatByDotReq);
        return improvements;
    }

    public ResponseImprovementDetail getImprovementDetail(Long contentId) {
        ResponseImprovementDetail responseImprovementDetail = queryFactory
                .select(
                        Projections.constructor(
                                ResponseImprovementDetail.class,
                                member.nickname,
                                improvement.createDate.stringValue(),
                                improvement.createdIp,
                                improvement.title,
                                improvement.content,
                                new CaseBuilder()
                                        .when(improvement.improvementStatus.eq(ImprovementStatus.RECEIVED))
                                        .then("접수")
                                        .when(improvement.improvementStatus.eq(ImprovementStatus.PENDING))
                                        .then("대기")
                                        .otherwise("완료"),
                                new CaseBuilder()
                                        .when(improvement.importantStatus.eq(ImportantStatus.HIGH))
                                        .then("높음")
                                        .when(improvement.importantStatus.eq(ImportantStatus.NORMAL))
                                        .then("중간")
                                        .otherwise("낮음")
                        ))
                .from(improvement)
                .join(member)
                .on(member.eq(improvement.member))
                .where(improvement.id.eq(contentId))
                .fetchOne();
        responseImprovementDetail.updateCreateDate(DateFormatUtil.formatByDotAndSlash.format(
                LocalDateTime.parse(responseImprovementDetail.getCreateDate()
                        , DateFormatUtil.FLEXIBLE_NANO_FORMATTER)));
        responseImprovementDetail.updateAdminMemoList(
                createAdminMemo.getAdminContentMemo(StaticDataType.Improvement,
                        contentId, queryFactory));

        return responseImprovementDetail;
    }

    public Page<ResponseMemberImproveList> getMemberImprovementList(RequestMemberImproveList requestMemberImproveList) {
        Pageable pageable = PageRequest.of(requestMemberImproveList.getOffSet(), 10);
        List<ResponseMemberImproveList> responseMemberImproveLists = queryFactory
                .select(
                        Projections.constructor(ResponseMemberImproveList.class,
                                improvement.id,
                                new CaseBuilder()
                                        .when(improvement.improvementStatus.eq(ImprovementStatus.RECEIVED))
                                        .then("접수")
                                        .when(improvement.improvementStatus.eq(ImprovementStatus.PENDING))
                                        .then("대기")
                                        .otherwise("완료"),
                                new CaseBuilder()
                                        .when(improvement.importantStatus.eq(ImportantStatus.HIGH))
                                        .then("높음")
                                        .when(improvement.importantStatus.eq(ImportantStatus.NORMAL))
                                        .then("중간")
                                        .otherwise("낮음"),
                                improvementCount.recommendCount,
                                member.nickname,
                                improvement.title,
                                improvement.content.substring(0, 10),
                                improvement.createDate.stringValue()
                        ))
                .from(improvement)
                .join(member)
                .on(member.eq(improvement.member))
                .join(improvementCount)
                .on(improvementCount.improvement.eq(improvement))
                .where(member.publicId.eq(requestMemberImproveList.getPublicId()))
                .orderBy(improvement.createDate.desc())
                .limit(10)
                .offset(pageable.getOffset())
                .fetch();

        DateFormatUtil.makeTimeByFormatter(responseMemberImproveLists,DateFormatEnum.formatByDotReq);

        Long totCount = Optional.ofNullable(queryFactory
                .select(improvement.count())
                .from(improvement)
                .join(member)
                .on(improvement.member.eq(member))
                .where(member.publicId.eq(requestMemberImproveList.getPublicId()))
                .fetchOne()).orElse(0L);

        return new PageImpl<>(responseMemberImproveLists, pageable, totCount);
    }


    private Predicate titleSearchCond(String searchKeyWord) {
        if (searchKeyWord == null) {
            return null;
        }
        return improvement.title.like("%" + searchKeyWord + "%");
    }

    private Predicate contentSearchCond(String searchKeyWord) {
        if (searchKeyWord == null) {
            return null;
        }
        return improvement.content.like("%" + searchKeyWord + "%");
    }


    private Predicate processStatusCond(ImprovementStatus improvementStatus) {
        if (improvementStatus.equals(ImprovementStatus.COMPLETED)) {

            return improvement.improvementStatus.eq(ImprovementStatus.COMPLETED);
        }
        if (improvementStatus.equals(ImprovementStatus.PENDING)) {
            return improvement.improvementStatus.eq(ImprovementStatus.PENDING);
        }
        if (improvementStatus.equals(ImprovementStatus.RECEIVED)) {
            return improvement.improvementStatus.eq(ImprovementStatus.RECEIVED);
        }
        return null;

    }

    private Predicate searchByWriter(String nickName) {
        if (nickName == null) {
            return null;
        }

        return member.nickname.like("%" + nickName + "%");
    }

    private Predicate searchByEmail(String email) {
        if (email == null) {
            return null;
        }

        return member.email.like("%" + email + "%");
    }

    private Predicate searchByImportantStatus(ImportantStatus status) {
        if (status == null) {
            return null;
        }
        if (status.equals(ImportantStatus.LOW)) {

            return improvement.importantStatus.eq(ImportantStatus.LOW);
        }
        if (status.equals(ImportantStatus.NORMAL)) {

            return improvement.importantStatus.eq(ImportantStatus.NORMAL);
        }

        return improvement.importantStatus.eq(ImportantStatus.HIGH);

    }

    private Predicate searchByTimeLine(LocalDateTime startTime, LocalDateTime endTime) {

        if (startTime != null & endTime == null) {

            return improvement.createDate.after(startTime);
        }
        if (endTime != null & startTime == null) {

            return improvement.createDate.before(endTime);
        }

        if (startTime == null & endTime == null) {

            return null;
        }
        return improvement.createDate.between(startTime, endTime);
    }

    private List<Query> filterQuery(RequestImprovementList requestImprovementList){
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

    public List<Query> makeMustQuery(RequestImprovementList requestImprovementList){
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
