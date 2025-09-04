package org.myteam.server.admin.repository;


import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.document.ContentDocument;
import org.myteam.server.admin.document.InquiryDocument;
import org.myteam.server.admin.entity.AdminContentMemo;
import org.myteam.server.admin.utill.CreateAdminMemo;
import org.myteam.server.admin.utill.enums.DateFormatEnum;
import org.myteam.server.admin.utill.enums.StaticDataType;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.myteam.server.match.match.domain.Match;
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
import java.util.stream.Collectors;

import static org.myteam.server.admin.dto.request.AdminMemoRequestDto.AdminMemoInquiryRequest;
import static org.myteam.server.admin.dto.response.InquiryResponseDto.*;
import static org.myteam.server.admin.dto.response.InquiryResponseDto.ResponseInquiryList;
import static org.myteam.server.admin.dto.response.InquiryResponseDto.ResponseInquiryListCond;
import static org.myteam.server.admin.dto.request.InquiryRequestDto.*;
import static org.myteam.server.inquiry.domain.QInquiry.inquiry;
import static org.myteam.server.member.entity.QMember.member;

@Repository
@RequiredArgsConstructor
@Transactional
public class InquirySearchRepo {

    private final JPAQueryFactory queryFactory;
    private final CreateAdminMemo createAdminMemo;
    private final ElasticsearchOperations elasticsearchOperations;

    public AdminContentMemo createAdminMemo(AdminMemoInquiryRequest adminMemoRequest) {

        return createAdminMemo.createInquiryAdminMemo(adminMemoRequest, queryFactory);
    }

    public ResponseInquiryDetail getInquiryDetail(Long inquiryId) {
        ResponseInquiryDetail responseInquiryDetail = queryFactory
                .select(
                        Projections.constructor(ResponseInquiryDetail.class,
                                inquiry.id,
                                new CaseBuilder()
                                        .when(inquiry.isAdminAnswered.isTrue())
                                        .then("답변완료")
                                        .otherwise("답변대기"),
                                inquiry.createdAt.stringValue(),
                                inquiry.clientIp,
                                new CaseBuilder()
                                        .when(member.isNull())
                                        .then("비회원")
                                        .otherwise("회원"),
                                new CaseBuilder()
                                        .when(member.isNull())
                                        .then("-")
                                        .otherwise(member.nickname),
                                inquiry.email,
                                inquiry.content
                        ))
                .from(inquiry)
                .leftJoin(member)
                .on(member.eq(inquiry.member))
                .where(inquiry.id.eq(inquiryId))
                .fetchOne();

        responseInquiryDetail.updateCreateDate(DateFormatUtil.formatByDot
                .format(LocalDateTime.parse(responseInquiryDetail.getCreateDate()
                        , DateFormatUtil.FLEXIBLE_NANO_FORMATTER)));
        responseInquiryDetail.updateAdminMemoList(
                createAdminMemo.getAdminContentMemo(StaticDataType.Inquiry,
                        inquiryId, queryFactory));

        return responseInquiryDetail;
    }

    public Page<ResponseInquiryList> getInquiryList(RequestInquiryList requestInquiryList) {

        Pageable pageable = PageRequest.of(requestInquiryList.getOffset(), 10);
        List<ResponseInquiryList> inquiryList = queryFactory
                .select(
                        Projections.constructor(ResponseInquiryList.class,
                                inquiry.id,
                                new CaseBuilder()
                                        .when(inquiry.isAdminAnswered.isTrue())
                                        .then("답변대기")
                                        .otherwise("답변완료"),
                                new CaseBuilder()
                                        .when(member.isNull())
                                        .then("비회원")
                                        .otherwise("회원"),
                                new CaseBuilder()
                                        .when(member.isNull())
                                        .then("-")
                                        .otherwise(member.nickname),
                                inquiry.email,
                                inquiry.content,
                                inquiry.createdAt.stringValue()
                        ))
                .from(inquiry)
                .leftJoin(member)
                .on(member.eq(inquiry.member))
                .where(inquiry.email.eq(requestInquiryList.getEmail()))
                .orderBy(inquiry.createdAt.desc())
                .limit(10)
                .offset(pageable.getOffset())
                .fetch();

        DateFormatUtil.makeTimeByFormatter(inquiryList, DateFormatEnum.formatByDotReq);
        Long totCount = Optional.ofNullable(queryFactory
                .select(inquiry.count())
                .from(inquiry)
                .leftJoin(member)
                .on(member.eq(inquiry.member))
                .where(inquiry.email.eq(requestInquiryList.getEmail()))
                .fetchOne()).orElse(0L);

        return new PageImpl<>(inquiryList, pageable, totCount);
    }

    public Page<ResponseInquiryListCond> getInquiryListByCond(RequestInquiryListCond requestInquiryListCond) {

        Pageable pageable = PageRequest.of(requestInquiryListCond.getOffset(), 10);

        List<ResponseInquiryListCond> responseInquiryListConds = queryFactory
                .select(Projections.constructor(ResponseInquiryListCond.class,
                                inquiry.id
                                , new CaseBuilder()
                                        .when(inquiry.isAdminAnswered.isTrue())
                                        .then("답변완료")
                                        .otherwise("답변대기"),
                                new CaseBuilder()
                                        .when(member.isNull())
                                        .then("비회원")
                                        .otherwise("회원"),
                                new CaseBuilder()
                                        .when(member.isNull())
                                        .then(inquiry.email)
                                        .otherwise(member.nickname),
                                inquiry.content,
                                inquiry.email,
                                inquiry.createdAt.stringValue()
                        )
                )
                .from(inquiry)
                .leftJoin(member)
                .on(inquiry.member.eq(member))
                .where(contentSearchCond(requestInquiryListCond.getContent()),
                        searchByWriter(requestInquiryListCond.getNickName()),
                        searchByTimeLine(requestInquiryListCond.provideStartTime()
                                , requestInquiryListCond.provideEndTime()),
                        searchByEmail(requestInquiryListCond.getEmail()),
                        processStatusCond(requestInquiryListCond.getIsAnswered())
                        , memberOrNot(requestInquiryListCond.getIsMember()))
                .orderBy(inquiry.createdAt.desc())
                .limit(10)
                .offset(pageable.getOffset())
                .fetch();

        DateFormatUtil.makeTimeByFormatter(responseInquiryListConds,DateFormatEnum.formatByDotReq);
        Long count = Optional.ofNullable(queryFactory.select(inquiry.count())
                .from(inquiry)
                .leftJoin(member)
                .on(inquiry.member.eq(member))
                .where(contentSearchCond(requestInquiryListCond.getContent()),
                        searchByWriter(requestInquiryListCond.getNickName()),
                        searchByTimeLine(requestInquiryListCond.provideStartTime(), requestInquiryListCond.provideEndTime()),
                        searchByEmail(requestInquiryListCond.getEmail()),
                        processStatusCond(requestInquiryListCond.getIsAnswered())
                        , memberOrNot(requestInquiryListCond.getIsMember()))
                .fetchOne()).orElse(0L);

        return new PageImpl<>(responseInquiryListConds, pageable, count);
    }

    public List<ResponseInquiryListCond> getInquiryListByContElasticSearch(RequestInquiryListCond requestInquiryListCond){
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

        DateFormatUtil.makeElasticTimeByFormatter(responseInquiryListConds,DateFormatEnum.formatByDotReq);

        return responseInquiryListConds;

    }


    private Predicate memberOrNot(Boolean isMember) {
        if (isMember == null) {
            return null;
        }
        if (isMember) {
            return inquiry.member.isNotNull();
        }
        return inquiry.member.isNull();
    }

    private Predicate contentSearchCond(String searchKeyWord) {
        if (searchKeyWord == null) {
            return null;
        }
        return inquiry.content.like("%" + searchKeyWord + "%");
    }

    private Predicate processStatusCond(Boolean completed) {
        if (completed == null) {
            return null;
        }
        if (completed) {
            return inquiry.isAdminAnswered.isTrue();
        }
        return inquiry.isAdminAnswered.isFalse();
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

    private Predicate searchByTimeLine(LocalDateTime startTime, LocalDateTime endTime) {

        if (startTime != null & endTime == null) {

            return inquiry.createdAt.after(startTime);
        }
        if (endTime != null & startTime == null) {

            return inquiry.createdAt.before(endTime);
        }

        if (startTime == null & endTime == null) {

            return null;
        }
        return inquiry.createdAt.between(startTime, endTime);

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
    public List<Query> makeMustQuery(RequestInquiryListCond requestInquiryListCond){
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
