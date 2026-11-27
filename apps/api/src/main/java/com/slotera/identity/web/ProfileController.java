package com.slotera.identity.web;

import com.slotera.identity.application.UserProfileService;
import com.slotera.identity.application.UserProfileService.UserProfile;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final UserProfileService profiles;

    public ProfileController(UserProfileService profiles) {
        this.profiles = profiles;
    }

    @GetMapping("/me")
    public UserProfile me(@AuthenticationPrincipal Jwt jwt) {
        return profiles.findOrCreate(jwt);
    }
}
