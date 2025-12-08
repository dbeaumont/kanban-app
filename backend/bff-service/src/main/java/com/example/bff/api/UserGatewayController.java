package com.example.bff.api;

import com.example.bff.dto.UserProfileDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserGatewayController {

    private final WebClient userWebClient;

    public UserGatewayController(@Qualifier("userWebClient") WebClient userWebClient) {
        this.userWebClient = userWebClient;
    }

    @GetMapping("/me")
    public Mono<UserProfileDto> me() {
        return userWebClient.get().uri("/users/me").retrieve().bodyToMono(UserProfileDto.class);
    }
}
