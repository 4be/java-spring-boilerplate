package com.vacant.account.services;


import com.vacant.account.utils.JsonMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class LoggingService {

    private static final List<String> excludePathPatterns = Arrays.asList("/actuator/**", "/swagger-ui/**");


    public void displayRequest(HttpServletRequest request, Object body) {
        var isMatch = excludePathPatterns.stream().anyMatch(exclude -> new AntPathMatcher().match(exclude, request.getRequestURI()));

        if (!isMatch) {
            Map<String, String> parameters = getParameters(request);
            Map<String, String> headers = getHeaders(request);
            var requestLog = new StringBuilder();
            requestLog.append("REQUEST=");
            requestLog.append("[").append(request.getMethod()).append("]");
            requestLog.append("[").append(request.getRequestURI()).append("] ");

            if (!headers.isEmpty()) {
                requestLog.append("RequestHeaders=[").append(headers).append("] ");
            }

            if (!parameters.isEmpty()) {
                requestLog.append("Parameters=[").append(parameters).append("] ");
            }

            if (!Objects.isNull(body)) {
                requestLog.append("RequestBody=[").append(JsonMapper.toString(body)).append("]");
            }

            log.info(requestLog.toString());
        }
    }


    public void displayResponse(HttpServletRequest request, HttpServletResponse response, Object body) {
        var isMatch = excludePathPatterns.stream().anyMatch(exclude -> new AntPathMatcher().match(exclude, request.getRequestURI()));

        if (!isMatch) {
            Map<String, String> headers = getHeaders(response);
            var responseLog = new StringBuilder();
            responseLog.append("RESPONSE=");
            responseLog.append("[").append(request.getMethod()).append("]");
            responseLog.append("[").append(request.getRequestURI()).append("] ");

            if (!headers.isEmpty()) {
                responseLog.append("ResponseHeaders=[").append(headers).append("] ");
            }

            responseLog.append("ResponseBody=[").append(JsonMapper.toString(body)).append("]");

            log.info(responseLog.toString());
        }
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        var headers = new HashMap<String, String>();
        Enumeration<String> header = request.getHeaderNames();

        while (header.hasMoreElements()) {
            var headerName = header.nextElement();
            var paramValue = headerName.equalsIgnoreCase(HttpHeaders.AUTHORIZATION) ? ellipsisText(request.getHeader(headerName)) :
                    request.getHeader(headerName);

            headers.put(headerName, paramValue);
        }

        return headers;
    }

    private Map<String, String> getHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        Collection<String> headerMap = response.getHeaderNames();
        for (String str : headerMap) {
            headers.put(str, response.getHeader(str));
        }
        return headers;
    }

    private Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();

        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            String paramValue = request.getParameter(paramName);
            parameters.put(paramName, paramValue);
        }

        return parameters;
    }

    private String ellipsisText(String value) {
        return value.length() >= 48 ?
                value.substring(0, 24) + "..." + value.substring(value.length() - 24) :
                value;
    }
}
