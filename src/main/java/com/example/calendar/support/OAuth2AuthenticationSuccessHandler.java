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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final CalendarService calendarService;

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

        // 2. Google Calendar API 호출 (비동기 처리)
        CompletableFuture<List<Event>> calendarEventsFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return calendarService.getUpcomingEventsForCurrentMonth(email);
            } catch (Exception e) {
                log.error("Failed to fetch Google Calendar events", e);
                return Collections.emptyList();
            }
        });

        // 3. JWT 응답 준비
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", jwtToken);

        try {
            // Calendar 이벤트 데이터를 동기적으로 가져옴
            List<Event> events = calendarEventsFuture.get(10, TimeUnit.SECONDS);
            responseBody.put("calendarEvents", events);
        } catch (Exception e) {
            log.error("Calendar API response timeout or failure", e);
            responseBody.put("calendarEvents", Collections.emptyList());
        }

        // 4. 응답 작성
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}

