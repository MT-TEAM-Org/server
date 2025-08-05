package org.myteam.server.admin.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.repository.AdminDashBoardRepository;
import org.myteam.server.admin.utill.enums.AdminDashBoardType;
import org.myteam.server.admin.utill.enums.DateType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import static org.myteam.server.admin.dto.response.AdminDashBoardResponseDto.ResponseLatestData;
import static org.myteam.server.admin.dto.response.AdminDashBoardResponseDto.ResponseStatic;

@Service
@RequiredArgsConstructor

public class AdminDashBoardService {

    private final AdminDashBoardRepository adminDashBoardRepository;


    public List<ResponseStatic> getStaticData(AdminDashBoardType adminDashBoardType, DateType dateType) {
        return adminDashBoardRepository.getStaticData(adminDashBoardType,dateType);

    }
    public Map<String,List<ResponseLatestData>> getLatestData() {
        return adminDashBoardRepository.getLatestData();
    }
}
