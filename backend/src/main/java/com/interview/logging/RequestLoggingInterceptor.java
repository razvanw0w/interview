package com.interview.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullPath = queryString == null ? uri : uri + "?" + queryString;

        log.info("Request started: method={}, path={}", method, fullPath);
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {
        Long startTime = (Long) request.getAttribute(START_TIME);
        long duration = startTime == null ? -1 : System.currentTimeMillis() - startTime;

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullPath = queryString == null ? uri : uri + "?" + queryString;

        if (ex != null) {
            log.warn(
                    "Request completed with exception: method={}, path={}, status={}, durationMs={}, error={}",
                    method,
                    fullPath,
                    response.getStatus(),
                    duration,
                    ex.getMessage()
            );
        } else {
            log.info(
                    "Request completed: method={}, path={}, status={}, durationMs={}",
                    method,
                    fullPath,
                    response.getStatus(),
                    duration
            );
        }
    }
}
