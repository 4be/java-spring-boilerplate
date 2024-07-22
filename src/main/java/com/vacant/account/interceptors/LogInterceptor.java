package com.vacant.account.interceptors;


import com.vacant.account.services.LoggingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LogInterceptor implements HandlerInterceptor {

    private final LoggingService loggingService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        response.addHeader("Request-ID", requestId);
        if (request.getContentLengthLong() <= 0) {
            loggingService.displayRequest(request, null);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.remove("requestId");
        MDC.remove("partnerReferenceNo");
    }
}
