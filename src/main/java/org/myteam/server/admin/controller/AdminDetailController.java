package org.myteam.server.admin.controller;


import com.esotericsoftware.kryo.serializers.FieldSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.myteam.server.admin.dto.AdminDetail;
import org.myteam.server.admin.service.AdminDetailService;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.global.web.response.ResponseStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.myteam.server.admin.dto.AdminDetail.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/detail")
public class AdminDetailController {


    private final AdminDetailService adminDetailService;



    @PostMapping("/board")
    public ResponseEntity<ResponseDto<ResponseContentDetail>> getBoardCommentDetail(@RequestBody RequestContentDetail requestContentDetail, BindingResult bindingResult){

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),
                "성공",adminDetailService.getCommentBoardDetail(requestContentDetail)));

    }


    @PostMapping("/inquiry/improvement")
    public ResponseEntity<ResponseDto<ResponseClient>> getInquiryImprovementDetail(@RequestBody RequestClient requestClient,BindingResult bindingResult){


        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),"성공",adminDetailService.getRquestClientDetail(requestClient)));
    }

    @PostMapping("/user")
    public ResponseEntity<ResponseDto<ResponseUserDetail>> getUserDetail(@RequestBody RequestUserDetail requestUserDetail,BindingResult bindingResult){

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),"성공" ,adminDetailService.getUserDetail(requestUserDetail)));
    }




    @PostMapping("/report")
    public ResponseEntity<ResponseDto<ResponseUserReportDetail>> getUserReportedDetail(@RequestBody RequestUserReportDetail requestUserReportDetail,BindingResult bindingResult){


        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),"ok" ,adminDetailService.getUserReportedDetail(requestUserReportDetail)));
    }

}
