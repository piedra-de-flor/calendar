package com.example.calendar.support;

import com.example.calendar.dto.member.JwtToken;
import com.example.calendar.service.schedule.google.GoogleAuthorizationService;
import com.google.api.client.auth.oauth2.Credential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleAuthorizationService authorizationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (email == null) {
            log.error("OAuth2 provider did not return email");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not provided by OAuth2 provider");
            return;
        }

        try {
            Credential credential = authorizationService.getCredentials(email);

            if (credential.getAccessToken() == null || (credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 3600)) {
                credential.refreshToken();
                log.info("Google Credential refreshed for email: {}", email);
            }
        } catch (GeneralSecurityException e) {
            log.error("Error refreshing Google credentials", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to refresh Google credentials");
            return;
        }

        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        String redirectUrl = "http://woodking2.site/main";
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        redirectUrl += "?accessToken=" + jwtToken.getAccessToken();

        log.info("Redirecting to: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}


