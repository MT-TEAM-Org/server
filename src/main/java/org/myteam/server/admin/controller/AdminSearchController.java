package org.myteam.server.admin.controller;


import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.dto.AdminDetail;
import org.myteam.server.admin.dto.AdminSearch;
import org.myteam.server.admin.service.AdminSearchService;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.global.web.response.ResponseStatus;
import org.myteam.server.member.domain.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.myteam.server.admin.dto.AdminSearch.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/list")
public class AdminSearchController {

    private final AdminSearchService adminSearchService;

    @PostMapping("/content")
    public ResponseEntity<ResponseDto<Page<ResponseSearchContent>>> getBoardCommentList(
            @RequestBody RequestSearchContent requestSearchContent, BindingResult bindingResult){

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),
                "조회성공",adminSearchService.getCommentBoardList(requestSearchContent)));


    }
    @PostMapping("/inquiry/improvement")
    public ResponseEntity<ResponseDto<Page<ResponseSearchInquiryImprovement>>> getInquiryImprovementList(
            @RequestBody RequestSearchInquiryImprovement requestSearchInquiryImprovement,
            BindingResult bindingResult){

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),
                "조회성공",adminSearchService.getInquiryImprovementList(requestSearchInquiryImprovement)));

    }

    @PostMapping("/user")
    public ResponseEntity<ResponseDto<Page<ResponseSearchUserList>>> getUserSearchList(
            @RequestBody RequestSearchUserList requestSearchUserList,BindingResult bindingResult){

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),"조회성공",adminSearchService.getUserSearchList(requestSearchUserList)));
    }

    @PostMapping("/reported")
    public ResponseEntity<ResponseDto<Page<ResponseUserReportedList>>> getUserReportedList(
            @RequestBody RequestUserReportedList requestUserReportedList,BindingResult bindingResult){

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),"조회성공",adminSearchService.getUserReportedList(requestUserReportedList)));
    }

    @PostMapping("/report")
    public ResponseEntity<ResponseDto<Page<ResponseReportList>>> getReportList(@RequestBody RequestReportList requestReportList,
            BindingResult bindingResult){

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(), "ok",adminSearchService.getReportList(requestReportList)));

    }



}
