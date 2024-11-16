package com.example.calendar.support;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class MemberEmailAspect {
    private final HttpServletRequest request;
    @Before("execution(* com.example.calendar.controller.*.*(..))")
    public void setMemberEmail() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String memberEmail = request.getUserPrincipal().getName();
        System.out.println(memberEmail);
        request.setAttribute("memberEmail", memberEmail);
    }
}