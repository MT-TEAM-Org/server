package org.myteam.server.admin.utill;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.entity.*;
import org.myteam.server.admin.repository.simpleRepo.*;
import org.myteam.server.board.domain.Board;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.myteam.server.admin.dto.request.AdminMemoRequestDto.*;
import static org.myteam.server.admin.dto.response.CommonResponseDto.*;
import static org.myteam.server.admin.entity.QAdminContentMemo.adminContentMemo;
import static org.myteam.server.admin.entity.QAdminMemberMemo.adminMemberMemo;
import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.comment.domain.QComment.comment1;
import static org.myteam.server.improvement.domain.QImprovement.improvement;
import static org.myteam.server.inquiry.domain.QInquiry.*;
import static org.myteam.server.member.entity.QMember.member;

@Component
@RequiredArgsConstructor
public class CreateAdminMemo {

    private final SecurityReadService securityReadService;
    private final MemberReadService memberReadService;
    private final AdminContentMemoRepo adminContentMemoRepo;
    private final AdminContentChangeLogRepo adminContentChangeLogRepo;
    private final AdminImproveChangeLogRepo adminImproveChangeLogRepo;
    private final AdminInquiryChangeLogRepo adminInquiryChangeLogRepo;
    private final AdminMemberMemoRepo adminMemberMemoRepo;
    private final AdminMemberChangeLogRepo adminMemberChangeLogRepo;
    public void createContentAdminMemo(AdminMemoContentRequest adminMemoRequest, JPAQueryFactory queryFactory) {
        //이거 뒤에줄을 playhive계정으로 만들어논 관리자를 넣어주도록하자.
        Member admin=adminMemoRequest.getAuto()==null ? securityReadService.getMember()
                :memberReadService.getAdminBot();

        AdminContentMemo adminMemo1;
        if (adminMemoRequest.getContent() != null) {
            adminMemo1 = AdminContentMemo
                    .builder()
                    .content(adminMemoRequest.getContent())
                    .contentId(adminMemoRequest.getContentId())
                    .staticDataType(adminMemoRequest.getStaticDataType())
                    .writer(admin)
                    .build();
            adminMemo1=adminContentMemoRepo.save(adminMemo1);
        }
        if (adminMemoRequest.getStaticDataType().name().equals(StaticDataType.COMMENT.name())) {
            Comment comment = queryFactory.select(comment1)
                    .from(comment1)
                    .where(comment1.id.eq(adminMemoRequest.getContentId()))
                    .fetchOne();
            if(!comment.getAdminControlType().equals(adminMemoRequest.getAdminControlType())){
                comment.updateAdminControlType(adminMemoRequest.getAdminControlType());
                AdminContentChangeLog adminChangeLog = AdminContentChangeLog
                        .builder()
                        .admin(admin)
                        .contentId(adminMemoRequest.getContentId())
                        .adminControlType(adminMemoRequest.getAdminControlType())
                        .staticDataType(StaticDataType.COMMENT)
                        .build();
                adminContentChangeLogRepo.save(adminChangeLog);
            }
            return ;
        }
        if (adminMemoRequest.getStaticDataType().name().equals(StaticDataType.BOARD.name())) {
            Board board1 = queryFactory.select(board)
                    .from(board)
                    .where(board.id.eq(adminMemoRequest.getContentId()))
                    .fetchFirst();
            if(!board1.getAdminControlType().equals(adminMemoRequest.getAdminControlType())){
                board1.updateAdminControlType(adminMemoRequest.getAdminControlType());
                AdminContentChangeLog adminChangeLog = AdminContentChangeLog
                        .builder()
                        .admin(admin)
                        .contentId(adminMemoRequest.getContentId())
                        .adminControlType(adminMemoRequest.getAdminControlType())
                        .staticDataType(StaticDataType.BOARD)
                        .build();
                adminContentChangeLogRepo.save(adminChangeLog);
            }
        }
    }

    public void createImprovementMemo(AdminMemoImprovementRequest adminMemoRequest
            ,JPAQueryFactory queryFactory) {
        Member admin=adminMemoRequest.getAuto()==null ? securityReadService.getMember()
                :memberReadService.getAdminBot();
        AdminContentMemo adminMemo1=null;
        if (adminMemoRequest.getContent() != null) {
            adminMemo1= AdminContentMemo
                    .builder()
                    .content(adminMemoRequest.getContent())
                    .contentId(adminMemoRequest.getContentId())
                    .staticDataType(StaticDataType.Improvement)
                    .writer(admin)
                    .build();
           adminContentMemoRepo.save(adminMemo1);
        }
        if(adminMemoRequest.getAuto()!=null){
            AdminImproveChangeLog adminChangeLog = AdminImproveChangeLog
                    .builder()
                    .admin(admin)
                    .contentId(adminMemoRequest.getContentId())
                    .improvementStatus(adminMemoRequest.getImprovementStatus())
                    .importantStatus(adminMemoRequest.getImportantStatus())
                    .build();
            adminImproveChangeLogRepo.save(adminChangeLog);
            return;
        }
        Improvement improvement1 = queryFactory.select(improvement)
                    .from(improvement)
                    .where(improvement.id.eq(adminMemoRequest.getContentId()))
                    .fetchOne();
        if (!improvement1.getImprovementStatus().name()
                    .equals(adminMemoRequest.getImprovementStatus().name())
                    ||!improvement1.getImportantStatus().name()
                    .equals(adminMemoRequest.getImportantStatus().name())) {
            AdminImproveChangeLog adminChangeLog = AdminImproveChangeLog
                    .builder()
                    .admin(admin)
                    .contentId(adminMemoRequest.getContentId())
                    .improvementStatus(adminMemoRequest.getImprovementStatus())
                    .importantStatus(adminMemoRequest.getImportantStatus())
                    .build();
            adminImproveChangeLogRepo.save(adminChangeLog);
            improvement1.updateImportantStatus(adminMemoRequest.getImportantStatus());
            improvement1.updateState(adminMemoRequest.getImprovementStatus());
        }

    }

    public AdminContentMemo createInquiryAdminMemo(AdminMemoInquiryRequest adminMemoRequest,JPAQueryFactory queryFactory) {
        Member admin=securityReadService.getMember();
        Inquiry inquiry1=queryFactory
                .select(inquiry)
                .from(inquiry)
                .where(inquiry.id.eq(adminMemoRequest.getContentId()))
                .fetchOne();
        AdminContentMemo adminMemo1=AdminContentMemo
                    .builder()
                    .content(adminMemoRequest.getContent())
                    .contentId(adminMemoRequest.getContentId())
                    .staticDataType(StaticDataType.Inquiry)
                    .writer(admin)
                    .build();
        adminMemo1=adminContentMemoRepo.save(adminMemo1);
        if(!inquiry1.isAdminAnswered()){
                inquiry1.updateAdminAnswered();
                AdminInquiryChangeLog adminInquiryChangeLog=AdminInquiryChangeLog
                                .builder()
                                .isMember(inquiry1.getMember()!=null)
                                .isAnswered(true)
                                .contentId(adminMemoRequest.getContentId())
                                .admin(admin)
                                .build();
            adminInquiryChangeLogRepo.save(adminInquiryChangeLog);
        }
        return adminMemo1;
    }

    public void adminBotCreateInquiryLog(AdminMemoInquiryRequest adminMemoRequest){
        Member admin=memberReadService.getAdminBot();
        AdminInquiryChangeLog adminInquiryChangeLog=AdminInquiryChangeLog
                .builder()
                .isMember(adminMemoRequest.getIsMember())
                .isAnswered(false)
                .contentId(adminMemoRequest.getContentId())
                .admin(admin)
                .build();
        adminInquiryChangeLogRepo.save(adminInquiryChangeLog);
    }

    public void createMemberMemo(Member writer, UUID publicId, String content,
                                 MemberStatus targetStatus,MemberStatus memberStatus){
        AdminMemberMemo adminMemberMemo1=AdminMemberMemo
                .builder()
                .memberId(publicId)
                .content(content)
                .writer(writer)
                .build();
        adminMemberMemoRepo.save(adminMemberMemo1);
        if(!targetStatus.equals(memberStatus)){
            AdminMemberChangeLog adminMemberChangeLog1= AdminMemberChangeLog
                            .builder()
                            .memberId(publicId)
                            .admin(writer)
                            .memberStatus(memberStatus)
                            .build();
            adminMemberChangeLogRepo.save(adminMemberChangeLog1);
        }

    }

    public List<AdminMemoResponse> getAdminContentMemo(StaticDataType staticDataType,Long contentId,JPAQueryFactory queryFactory){
        List<AdminMemoResponse> adminMemoList=queryFactory.select(
                        Projections.constructor(AdminMemoResponse.class,
                                member.nickname,
                                adminContentMemo.createDate.stringValue(),
                                adminContentMemo.content
                                )
                )
                .from(adminContentMemo)
                .join(member)
                .on(member.eq(adminContentMemo.writer))
                .where(adminContentMemo.staticDataType.eq(staticDataType)
                        .and(adminContentMemo.contentId.eq(contentId)))
                .orderBy(adminContentMemo.createDate.desc())
                .fetch();
        adminMemoList.stream().forEach(x->{
            x.updateCreateDate(DateFormatUtil.formatByDot
                    .format(LocalDateTime.parse(x.getCreateDate(),
                            DateFormatUtil.FLEXIBLE_NANO_FORMATTER)));
        });
        return adminMemoList;
    }

    public List<AdminMemoResponse> getAdminMemberMemo(UUID memberId, JPAQueryFactory queryFactory){
        List<AdminMemoResponse> adminMemoList=queryFactory.select(
                        Projections.constructor(AdminMemoResponse.class,
                                member.nickname,
                                adminMemberMemo.createDate.stringValue(),
                                adminMemberMemo.content
                        )
                )
                .from(adminMemberMemo)
                .join(member)
                .on(adminMemberMemo.writer.eq(member))
                .where(adminMemberMemo.memberId.eq(memberId))
                .orderBy(adminMemberMemo.createDate.desc())
                .fetch();
        adminMemoList.stream().forEach(x->{
            x.updateCreateDate(DateFormatUtil.formatByDot
                    .format(LocalDateTime.parse(x.getCreateDate(),
                            DateFormatUtil.FLEXIBLE_NANO_FORMATTER)));
        });
        return adminMemoList;
    }

}
