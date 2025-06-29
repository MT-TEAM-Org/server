package org.myteam.server.admin.controller;


import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.service.AdminStaticService;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.global.web.response.ResponseStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import static org.myteam.server.admin.dto.AdminStatic.*;


@RequestMapping("/api/static")
@RestController
@RequiredArgsConstructor
public class AdminStaticController {

    private final AdminStaticService adminStaticService;



    @PostMapping("/data")
    public ResponseEntity<ResponseDto<ResponseStaticData>> Get_Report_Static(@RequestBody  RequestStaticData requestReportStatic, BindingResult bindingResult){


        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),"통계 불러오기 성공",adminStaticService.Get_Static_Data(requestReportStatic)));

    }

    @PostMapping("/latest")
    public ResponseEntity<ResponseDto<List<LatestData>>> Get_Latest_report_list(@RequestBody RequestLatestData requestLatestData,BindingResult bindingResult){

        return adminStaticService.getLatestList(requestLatestData);
    }




    /*
    *
    * 방문자수 하고 삭제된 멤버 통계쿼리들도 생각을 해줘야될듯...?
    *
    * */


}
