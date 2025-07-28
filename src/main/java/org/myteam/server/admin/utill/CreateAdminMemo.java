package org.myteam.server.admin.utill;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.entity.AdminChangeLog;
import org.myteam.server.admin.entity.AdminMemo;
import org.myteam.server.admin.repository.simpleRepo.AdminChangeLogRepo;
import org.myteam.server.admin.repository.simpleRepo.AdminMemoRepository;
import org.myteam.server.board.domain.Board;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

import static org.myteam.server.admin.dto.request.AdminMemoRequestDto.*;
import static org.myteam.server.admin.dto.response.CommonResponseDto.*;
import static org.myteam.server.admin.entity.QAdminMemo.*;
import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.comment.domain.QComment.comment1;
import static org.myteam.server.improvement.domain.QImprovement.improvement;
import static org.myteam.server.inquiry.domain.QInquiry.*;
import static org.myteam.server.member.entity.QMember.member;

@Component
@RequiredArgsConstructor
public class CreateAdminMemo {

    private final SecurityReadService securityReadService;
    private final AdminMemoRepository adminMemoRepository;
    private final AdminChangeLogRepo adminChangeLogRepo;
    public void createContentAdminMemo(AdminMemoContentRequest adminMemoRequest, JPAQueryFactory queryFactory) {
        Member admin = securityReadService.getMember();
        AdminMemo adminMemo1=null;
        if(adminMemoRequest.getContent()==null){
            adminMemo1 = AdminMemo
                    .builder()
                    .contentId(adminMemoRequest.getContentId())
                    .staticDataType(adminMemoRequest.getStaticDataType())
                    .writer(admin)
                    .build();
            adminMemo1=adminMemoRepository.save(adminMemo1);
        }
        if (adminMemoRequest.getContent() != null) {
            adminMemo1 = AdminMemo
                    .builder()
                    .content(adminMemoRequest.getContent())
                    .contentId(adminMemoRequest.getContentId())
                    .staticDataType(adminMemoRequest.getStaticDataType())
                    .writer(admin)
                    .build();
            adminMemo1=adminMemoRepository.save(adminMemo1);
        }
        if (adminMemoRequest.getStaticDataType().name().equals(StaticDataType.COMMENT.name())) {
            Comment comment = queryFactory.select(comment1)
                    .from(comment1)
                    .where(comment1.id.eq(adminMemoRequest.getContentId()))
                    .fetchOne();
            if(!comment.getAdminControlType().equals(adminMemoRequest.getAdminControlType())){
                comment.updateAdminControlType(adminMemoRequest.getAdminControlType());
                AdminChangeLog adminChangeLog = AdminChangeLog
                        .builder()
                        .admin(admin)
                        .contentId(adminMemoRequest.getContentId())
                        .adminControlType(adminMemoRequest.getAdminControlType())
                        .staticDataType(StaticDataType.COMMENT)
                        .build();
                adminChangeLogRepo.save(adminChangeLog);
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
                AdminChangeLog adminChangeLog = AdminChangeLog
                        .builder()
                        .admin(admin)
                        .contentId(adminMemoRequest.getContentId())
                        .adminControlType(adminMemoRequest.getAdminControlType())
                        .staticDataType(StaticDataType.COMMENT)
                        .build();
                adminChangeLogRepo.save(adminChangeLog);
            }
        }
    }

    public void createImprovementMemo(AdminMemoImprovementRequest adminMemoRequest
            ,JPAQueryFactory queryFactory) {
        Member  admin = securityReadService.getMember();
        AdminMemo adminMemo1=null;
        if(adminMemoRequest.getContent()==null){
            adminMemo1 = AdminMemo
                    .builder()
                    .contentId(adminMemoRequest.getContentId())
                    .staticDataType(StaticDataType.Improvement)
                    .writer(admin)
                    .build();
           adminMemoRepository.save(adminMemo1);
        }
        if (adminMemoRequest.getContent() != null) {
            adminMemo1= AdminMemo
                    .builder()
                    .content(adminMemoRequest.getContent())
                    .contentId(adminMemoRequest.getContentId())
                    .staticDataType(StaticDataType.Improvement)
                    .writer(admin)
                    .build();
           adminMemoRepository.save(adminMemo1);
        }
        Improvement improvement1 = queryFactory.select(improvement)
                    .from(improvement)
                    .where(improvement.id.eq(adminMemoRequest.getContentId()))
                    .fetchOne();
        if (!improvement1.getImprovementStatus().name()
                    .equals(adminMemoRequest.getImprovementStatus().name())
                    ||!improvement1.getImportantStatus().name()
                    .equals(adminMemoRequest.getImportantStatus().name())) {
                improvement1.updateImportantStatus(adminMemoRequest.getImportantStatus());
                improvement1.updateState(adminMemoRequest.getImprovementStatus());
        }

    }

    public AdminMemo createInquiryAdminMemo(AdminMemoInquiryRequest adminMemoRequest,JPAQueryFactory queryFactory) {
        Member  admin = securityReadService.getMember();
        AdminMemo adminMemo1=null;
        if(adminMemoRequest.getContent()==null){
            adminMemo1 = AdminMemo
                    .builder()
                    .contentId(adminMemoRequest.getContentId())
                    .staticDataType(StaticDataType.Inquiry)
                    .writer(admin)
                    .build();
            adminMemo1=adminMemoRepository.save(adminMemo1);
        }
        if (adminMemoRequest.getContent() != null) {
            adminMemo1= AdminMemo
                    .builder()
                    .content(adminMemoRequest.getContent())
                    .contentId(adminMemoRequest.getContentId())
                    .staticDataType(StaticDataType.Inquiry)
                    .writer(admin)
                    .build();
            adminMemo1=adminMemoRepository.save(adminMemo1);
        }

        Inquiry inquiry1=queryFactory
                    .select(inquiry)
                    .from(inquiry)
                    .where(inquiry.id.eq(adminMemoRequest.getContentId()))
                    .fetchOne();
        if(!inquiry1.isAdminAnswered()){
                inquiry1.updateAdminAnswered();
        }

        return adminMemo1;
    }

    public List<AdminMemoResponse> getAdminMemo(StaticDataType staticDataType,Long contentId,JPAQueryFactory queryFactory){
        List<AdminMemoResponse> adminMemoList=queryFactory.select(
                        Projections.constructor(AdminMemoResponse.class,
                                member.nickname,
                                adminMemo.createDate.stringValue(),
                                adminMemo.content
                                )
                )
                .from(adminMemo)
                .join(member)
                .on(member.eq(adminMemo.writer))
                .where(adminMemo.staticDataType.eq(staticDataType)
                        .and(adminMemo.contentId.eq(contentId)))
                .orderBy(adminMemo.createDate.desc())
                .fetch();
        adminMemoList.stream().forEach(x->{
            x.updateCreateDate(DateFormatUtil.formatByDot
                    .format(LocalDateTime.parse(x.getCreateDate(),
                            DateFormatUtil.FLEXIBLE_NANO_FORMATTER)));
        });
        return adminMemoList;
    }

}
