package com.sagatrading.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Value("${clientService.verificationUrl}")
    private String AUTH_SERVER_URL;
    private static final String AUTH_TOKEN_HEADER = "Authorization";

    @Override
    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        // Implement your authentication logic here
        if (!requiresAuthentication(request)) {
            System.out.println("Does not require auth");
            return true; // Allow the request to proceed to the controller
        } else {
            // Make an HTTP request to your authentication server to validate the request
            System.out.println("Require authentication");
            String token = request.getHeader(AUTH_TOKEN_HEADER);
            System.out.println("Auth token header: " + AUTH_TOKEN_HEADER + " " + AUTH_SERVER_URL + " " + token);
            boolean isAuthenticated = validateToken(token);
            if (isAuthenticated) {
                return true; // Allow the request to proceed to the controller
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return false; // Stop further processing of the request
            }
        }
    }

    @Override
    public void postHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        // Implement any post-processing logic if needed
    }

    @Override
    public void afterCompletion(HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        // Implement any cleanup logic if needed
    }

    private boolean requiresAuthentication(jakarta.servlet.http.HttpServletRequest request) {
        // Define the routes/controllers that require authentication
        String requestURI = request.getRequestURI();
        // For example, if you want to authenticate all routes under "/api"
        return requestURI.startsWith("/");
    }

    private boolean validateToken(String token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTH_TOKEN_HEADER, token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(AUTH_SERVER_URL, HttpMethod.GET, entity, String.class);
        return response.getStatusCode().is2xxSuccessful();
    }
}
