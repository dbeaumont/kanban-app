package com.example.bff.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * Returns 401 for API calls, otherwise redirects to the OAuth2 login entrypoint.
 */
public class ApiAwareAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final String loginPath;

    public ApiAwareAuthenticationEntryPoint(String loginPath) {
        this.loginPath = loginPath;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String uri = request.getRequestURI();
        boolean isApi = uri != null && uri.startsWith("/api/");
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
        if (isApi || isAjax) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            response.sendRedirect(loginPath);
        }
    }
}
