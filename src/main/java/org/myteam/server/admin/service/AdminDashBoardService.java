package org.myteam.server.admin.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.repository.AdminDashBoardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.myteam.server.admin.dto.request.AdminDashBoardRequestDto.RequestLatestData;
import static org.myteam.server.admin.dto.request.AdminDashBoardRequestDto.RequestStatic;
import static org.myteam.server.admin.dto.response.AdminDashBoardResponseDto.ResponseLatestData;
import static org.myteam.server.admin.dto.response.AdminDashBoardResponseDto.ResponseStatic;

@Service
@RequiredArgsConstructor

public class AdminDashBoardService {

    private final AdminDashBoardRepository adminDashBoardRepository;


    public ResponseStatic getStaticData(RequestStatic requestStatic) {

        return adminDashBoardRepository.getStaticData(requestStatic);

    }

    public List<ResponseLatestData> getLatestData(RequestLatestData requestLatestData) {

        return adminDashBoardRepository.getLatestData(requestLatestData);
    }
}
