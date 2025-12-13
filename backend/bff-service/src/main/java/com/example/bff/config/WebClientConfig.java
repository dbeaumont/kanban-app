package com.example.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${app.services.board-url}")
    private String boardServiceUrl;
    @Value("${app.services.task-url}")
    private String taskServiceUrl;
    @Value("${app.services.user-url}")
    private String userServiceUrl;

    @Bean(name = "boardWebClient")
    public WebClient boardWebClient(ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2) {
        return WebClient.builder()
                .baseUrl(boardServiceUrl)
                .apply(oauth2.oauth2Configuration())
                .build();
    }

    @Bean(name = "taskWebClient")
    public WebClient taskWebClient(ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2) {
        return WebClient.builder()
                .baseUrl(taskServiceUrl)
                .apply(oauth2.oauth2Configuration())
                .build();
    }

    @Bean(name = "userWebClient")
    public WebClient userWebClient(ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2) {
        return WebClient.builder()
                .baseUrl(userServiceUrl)
                .apply(oauth2.oauth2Configuration())
                .build();
    }

    @Bean
    public ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Filter(ClientRegistrationRepository registrations,
                                                                            OAuth2AuthorizedClientRepository clientRepository,
                                                                            OAuth2AuthorizedClientService clientService) {
        // Use the session-backed repository so we reuse the user's login token for downstream calls
        OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder()
                .authorizationCode()
                .refreshToken()
                .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(registrations, clientService);
        manager.setAuthorizedClientProvider(provider);

        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(manager);
        oauth2.setDefaultOAuth2AuthorizedClient(true);
        oauth2.setDefaultClientRegistrationId("keycloak");
        return oauth2;
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService clientService) {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(clientService);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository registrations) {
        return new InMemoryOAuth2AuthorizedClientService(registrations);
    }
}
