package org.myteam.server.admin.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.dto.AdminDashBorad;
import org.myteam.server.admin.repository.AdminDashBoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.myteam.server.admin.dto.AdminDashBorad.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminDashBoardService {

    private final AdminDashBoardRepository adminDashBoardRepository;


    public ResponseStatic getStaticData(RequestStatic requestStatic){

       return adminDashBoardRepository.getStaticData(requestStatic);

    }

    public List<ResponseLatestData> getLatestData(RequestLatestData requestLatestData){

        return adminDashBoardRepository.getLatestData(requestLatestData);
    }
}
