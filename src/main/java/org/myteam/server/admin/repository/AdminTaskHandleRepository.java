package org.myteam.server.admin.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.dto.AdminTask;
import org.myteam.server.admin.entity.AdminResponseMemo;
import org.myteam.server.admin.entity.MemberMemo;
import org.myteam.server.admin.utils.InquiryMailStrategy;
import org.myteam.server.admin.utils.StaticDataType;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.repository.BoardRepository;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.dto.request.CommentRequest;
import org.myteam.server.comment.repository.CommentRepository;
import org.myteam.server.comment.service.CommentService;
import org.myteam.server.common.certification.service.CertificationService;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.repository.ImprovementRepository;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Repository;

import java.sql.PseudoColumnUsage;
import java.util.Optional;
import java.util.UUID;

import static org.myteam.server.admin.dto.AdminTask.*;
import static org.myteam.server.comment.dto.request.CommentRequest.*;


@Repository
@RequiredArgsConstructor

public class AdminTaskHandleRepository {



    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final AdminResponseMemoRepo adminResponseMemoRepo;
    private final InquiryMailStrategy inquiryMailStrategy;
    private final InquiryRepository inquiryRepository;
    private final ImprovementRepository improvementRepository;
    private final CommentService commentService;
    private final MemberMemoRepository memberMemoRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final SecurityReadService securityReadService;
    private final MemberReadService memberReadService;



    public String handleImprovementInquiry(RequestHandleInquiryImprovement requestHandleInquiryImprovement,String ip){


        taskByDataType(requestHandleInquiryImprovement,ip);


        return "ok";

    }


    public String ContentAdminControlTaskHandle(RequestHandleContent requestHandleContent){

           if(requestHandleContent.getStaticDataType().name().equals(StaticDataType.CommentData.name())){
               Optional<Comment> comment=commentRepository.findById(requestHandleContent.getContentId());

               if(comment.isEmpty()){
                   throw new PlayHiveException(ErrorCode.COMMENT_NOT_FOUND);
               }

               comment.get().updateCommentAdminControlType(requestHandleContent.getAdminControlType());

               commentRepository.save(comment.get());

               return "ok";

           }

            Optional<Board> board=boardRepository.findById(requestHandleContent.getContentId());

            if(board.isEmpty()){
                throw new PlayHiveException(ErrorCode.BOARD_NOT_FOUND);
            }
            board.get().updateBoardAdminControlType(requestHandleContent.getAdminControlType());

            boardRepository.save(board.get());

            return "ok";


    }


    public String handleMemberControlTask(RequestHandleMember requestHandleMember){
            Member member=memberReadService.findById(requestHandleMember.getMemberId());
            member.updateStatus(requestHandleMember.getMemberStatus());
            Optional<MemberMemo> memberMemo=memberMemoRepository.findByReportedId(requestHandleMember.getMemberId());

            if(memberMemo.isEmpty()){

                MemberMemo memberMemo1=MemberMemo.builder()
                        .uuid(requestHandleMember.getMemberId())
                        .content("")
                        .build();
                memberMemoRepository.save(memberMemo1);

                memberMemo1.updateContent(requestHandleMember.getContent());


                memberMemoRepository.save(memberMemo1);
                memberJpaRepository.save(member);
                return "ok";
            }

           memberMemo.get().updateContent(requestHandleMember.getContent());
            memberMemoRepository.save(memberMemo.get());
            memberJpaRepository.save(member);


            return "ok";
    }


    private void taskByDataType(RequestHandleInquiryImprovement requestHandleInquiryImprovement,String ip){
        if(requestHandleInquiryImprovement.getStaticDataType().name().equals(StaticDataType.InquiryData.name())){

            Member member= securityReadService.getMember();


            Optional<Inquiry> inquiry=inquiryRepository.findById(requestHandleInquiryImprovement.getContentId());
            if(inquiry.isEmpty()){
                throw new PlayHiveException(ErrorCode.INQUIRY_NOT_FOUND);
            }

            inquiryMailStrategy.send(requestHandleInquiryImprovement.getReceiverEmail(),inquiry.get().getContent(),requestHandleInquiryImprovement.getResponseContent());
            inquiry.get().updateAdminAnswered(true);
            inquiryRepository.save(inquiry.get());

            AdminResponseMemo adminResponseMemo=AdminResponseMemo.builder()
                    .inquiryId(requestHandleInquiryImprovement.getContentId())
                    .content(requestHandleInquiryImprovement.getResponseContent())
                    .id(member.getPublicId())
                    .build();

            adminResponseMemoRepo.save(adminResponseMemo);


        }
        Optional<Improvement> improvement=improvementRepository.findById(requestHandleInquiryImprovement.getContentId());

        if(improvement.isEmpty()){
            throw new PlayHiveException(ErrorCode.IMPROVEMENT_NOT_FOUND);
        }
        improvement.get().updateState(requestHandleInquiryImprovement.getImprovementStatus());
        CommentSaveRequest commentSaveRequest=CommentSaveRequest.builder()
                .comment(requestHandleInquiryImprovement.getResponseContent())
                .type(CommentType.IMPROVEMENT)
                .build();

        commentService.addComment(requestHandleInquiryImprovement.getContentId(),commentSaveRequest,ip);

        improvementRepository.save(improvement.get());


    }


}
