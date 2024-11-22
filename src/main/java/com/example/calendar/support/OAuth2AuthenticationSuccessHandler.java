package com.example.calendar.support;

import com.example.calendar.dto.member.JwtToken;
import com.example.calendar.service.google.CalendarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.calendar.model.Event;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final CalendarService calendarService; // CalendarService 주입

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

        // 1. JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        // 2. Google Calendar API 호출
        List<Event> events;
        try {
            events = calendarService.getUpcomingEvents(email); // 캘린더 데이터 가져오기
        } catch (Exception e) {
            log.error("Failed to fetch Google Calendar events", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch Google Calendar events");
            return;
        }

        // 3. JWT와 캘린더 데이터 JSON 응답
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", jwtToken);
        responseBody.put("calendarEvents", events);

        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
