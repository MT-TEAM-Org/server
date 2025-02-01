package org.myteam.server.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.dto.request.FilterDataRequest;
import org.myteam.server.chat.service.FilterWriteService;
import org.myteam.server.global.web.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/filter")
public class FilterController {

    private final FilterWriteService filterWriteService;

    /**
     * 필터 데이터 추가
     */
    @PostMapping
    public ResponseEntity<ResponseDto<String>> addFilterData(@RequestBody FilterDataRequest filterData) {
        log.info("addFilterData: {}", filterData);

        filterWriteService.addFilteredWord(filterData.getFilterData());

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "Successfully Filter data added",
                filterData.getFilterData()
        ));
    }

    /**
     * 필터 데이터 삭제
     */
    @DeleteMapping
    public ResponseEntity<ResponseDto<String>> deleteFilterData(@RequestBody FilterDataRequest filterData) {
        log.info("deleteFilterData: {}", filterData);

        filterWriteService.removeFilteredWord(filterData.getFilterData());

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "Successfully Filter data deleted",
                filterData.getFilterData()
        ));
    }
}
