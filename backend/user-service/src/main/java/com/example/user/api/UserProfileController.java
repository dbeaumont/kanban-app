package com.example.user.api;

import com.example.user.application.UserProfileService;
import com.example.user.domain.UserProfile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserProfileController {
    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @GetMapping public List<UserProfile> all() { return service.findAll(); }

    @GetMapping("/{id}") public UserProfile one(@PathVariable("id") String id) { return service.findById(id); }

    @PostMapping public UserProfile create(@RequestBody UserProfile profile) { return service.create(profile); }

    @PutMapping("/{id}") public UserProfile update(@PathVariable("id") String id, @RequestBody UserProfile profile) { return service.update(id, profile); }

    @DeleteMapping("/{id}") public void delete(@PathVariable("id") String id) { service.delete(id); }

    @GetMapping("/me")
    public UserProfile me(@AuthenticationPrincipal Jwt jwt) {
        String sub = jwt.getSubject();
        return service.findByKeycloakId(sub);
    }
}
