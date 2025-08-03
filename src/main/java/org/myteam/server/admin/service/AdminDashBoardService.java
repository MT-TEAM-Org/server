package org.myteam.server.admin.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.repository.AdminDashBoardRepository;
import org.myteam.server.admin.utill.DateType;
import org.myteam.server.admin.utill.StaticDataType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.myteam.server.admin.dto.request.AdminDashBoardRequestDto.RequestLatestData;
import static org.myteam.server.admin.dto.request.AdminDashBoardRequestDto.RequestStatic;
import static org.myteam.server.admin.dto.response.AdminDashBoardResponseDto.ResponseLatestData;
import static org.myteam.server.admin.dto.response.AdminDashBoardResponseDto.ResponseStatic;

@Service
@RequiredArgsConstructor

public class AdminDashBoardService {

    private final AdminDashBoardRepository adminDashBoardRepository;


    public List<ResponseStatic> getStaticData(StaticDataType staticDataType,DateType dateType) {

        return adminDashBoardRepository.getStaticData(staticDataType,dateType);

    }

    public Map<String,List<ResponseLatestData>> getLatestData() {

        return adminDashBoardRepository.getLatestData();
    }
}
