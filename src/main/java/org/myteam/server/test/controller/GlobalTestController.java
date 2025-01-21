package org.myteam.server.test.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.test.dto.SlackTestRequest;
import org.myteam.server.util.slack.service.SlackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Tags(value = @Tag(name = "TestController", description = "Backend Test"))
public class GlobalTestController {

    private final SlackService slackService;


    @GetMapping("/all")
    public ResponseEntity<?> anyOneCanAccess() {
        log.info("all people can access");
        return ResponseEntity.ok("all people can access");
    }

    @GetMapping("/cert")
    public ResponseEntity<?> certifiedOne() {
        log.info("certified user");
        return ResponseEntity.ok("you are authenticated one");
    }

    @GetMapping("/admin")
    public ResponseEntity<?> certifiedAdmin() {
        log.info("certified admin");
        return ResponseEntity.ok("you are authenticated admin");
    }

    @GetMapping("/exception-test")
    public String exceptionTest() {
        log.info("exception test ");
        throw new PlayHiveException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/slack")
    public ResponseEntity<String> slackTest(@RequestBody SlackTestRequest slackTestRequest) {
        log.info("slack test");

        String message = String.format(
                "새로운 문의가 도착했습니다! \n이름: %s\n이메일: %s\n내용: %s",
                slackTestRequest.getName(),
                slackTestRequest.getEmail(),
                slackTestRequest.getMessage()
        );

        slackService.sendSlackNotification(message);

        return ResponseEntity.ok("문의가 접수되었습니다.");
    }
}
