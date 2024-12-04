package org.myteam.server.auth.test;

import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/user-access-test")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String userAccessTest(@AuthenticationPrincipal UUID publicId) {
        System.out.println("publicId = " + publicId);

        return "User Access Test";
    }

    @GetMapping("/manager-access-test")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String managerAccessTest(@AuthenticationPrincipal UUID publicId, Authentication authentication) {
        System.out.println("publicId = " + publicId);

        return "Manager Access Test";
    }
}
