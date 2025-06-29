package org.myteam.server.admin.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.dto.AdminUpdateDto;
import org.myteam.server.admin.service.AdminService;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.global.web.response.ResponseStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@Service
@RequiredArgsConstructor
@RequestMapping("/api/admin/member")
public class AdminInfoController {

    private final AdminService adminService;


    @PostMapping("/update/profile")
    public ResponseEntity<ResponseDto<String>> updateAdmin(@RequestBody @Valid AdminUpdateDto adminUpdateDto,
                                                           BindingResult bindingResult){

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),"정보 수정 성공",adminService.updateAdmin(adminUpdateDto)));

    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseDto<String>> getAdminProfile(){


        return ResponseEntity.ok(new ResponseDto(ResponseStatus.SUCCESS.name(),"성공",adminService.getAdminProfile()));

    }

    @GetMapping("/test")
    public ResponseEntity<ResponseDto<String>> test(){


        return ResponseEntity.ok(new ResponseDto(ResponseStatus.SUCCESS.name(),"성공","성공"));

    }
}
