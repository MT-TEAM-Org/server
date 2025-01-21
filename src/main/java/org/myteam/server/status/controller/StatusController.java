package org.myteam.server.status.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/status")
@Tags(value = @Tag(name = "StatusController", description = "Retrieve any status"))
public class StatusController {

    @GetMapping
    public ResponseEntity<?> healthCheck() {
        log.info("Health check endpoint accessed.");
        return ResponseEntity.ok("ok");
    }
}

