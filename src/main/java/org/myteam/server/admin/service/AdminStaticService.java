package org.myteam.server.admin.service;


import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.patterns.ScopeWithTypeVariables;
import org.myteam.server.admin.repository.AdminStaticRepository;
import org.myteam.server.admin.utils.DateTypeFactory;

import org.myteam.server.admin.utils.StaticUtil;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.global.web.response.ResponseStatus;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.myteam.server.admin.dto.AdminStatic.*;
import static org.myteam.server.report.dto.response.ReportResponse.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminStaticService {



    private final DateTypeFactory dateTypeFactory;

    private final AdminStaticRepository adminStaticRepository;

    public ResponseStaticData Get_Static_Data(RequestStaticData requestStaticData){
        LocalDateTime now=LocalDateTime.now();
        List<LocalDateTime> static_time_list=dateTypeFactory.SupplyDateTime(requestStaticData.getDateType(),now);


        List<Long> count_list=adminStaticRepository.getStaticData(requestStaticData.getStaticDataType(),static_time_list);

        int percent=StaticUtil.make_static_percent(count_list.get(0),count_list.get(1));


        ResponseStaticData responseStaticData=ResponseStaticData.builder()
                .now_count(count_list.get(0))
                .past_count(count_list.get(1))
                .percent(percent)
                .tot_count(count_list.get(2))
                .build();

        return responseStaticData;

    }

    public ResponseEntity<ResponseDto<List<LatestData>>> getLatestList(RequestLatestData requestLatestData){
        List<LatestData> reportLatestData=adminStaticRepository.getLatestReportList(requestLatestData.getStaticDataType());
        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),"통계 불러오기 성공",reportLatestData));

    }






}
