package org.myteam.server.admin.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.dto.AdminTask;
import org.myteam.server.admin.service.AdminTaskHandleService;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.global.web.response.ResponseStatus;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.myteam.server.admin.dto.AdminTask.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/task")
public class AdminTaskHandleController {

    private final AdminTaskHandleService adminTaskHandleService;


    @PostMapping("/inquiry/improvement")
    public ResponseEntity<ResponseDto<String>> InquiryImprovementTaskHandle(@RequestBody RequestHandleInquiryImprovement requestHandleInquiry, BindingResult bindingResult,HttpServletRequest httpServletRequest){

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),"성공",
                adminTaskHandleService.handleRequestInquiryImprovement(requestHandleInquiry,ClientUtils.getRemoteIP(httpServletRequest))));

    }


    @PostMapping("/content")
    public ResponseEntity<ResponseDto<String>> ContentAdminControlTaskHandle(@RequestBody RequestHandleContent requestHandleContent,BindingResult bindingResult){

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),
                "ok",adminTaskHandleService.ContentAdminControlTaskHandle(requestHandleContent)));

    }

    @PostMapping("/user")
    public ResponseEntity<ResponseDto<String>> MemberAdminControlTaskHandle(@RequestBody RequestHandleMember requestHandleMember
    ,BindingResult bindingResult){

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),
                "ok",adminTaskHandleService.ContentMemberControlTaskHandle(requestHandleMember)));
    }


}
