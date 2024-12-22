package com.astroverse.backend.component;

import com.astroverse.backend.service.TokenBlackListService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestInterceptor implements HandlerInterceptor {
    private final TokenBlackListService tokenBlackListService;

    public RequestInterceptor(TokenBlackListService tokenBlackListService) {
        this.tokenBlackListService = tokenBlackListService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!isValidRequest(request)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Richiesta non valida");
            return false;
        }
        return true;
    }

    private boolean isValidRequest(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        return !tokenBlackListService.existToken(accessToken);
    }
}