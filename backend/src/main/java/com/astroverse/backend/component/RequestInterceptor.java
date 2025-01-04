package com.astroverse.backend.component;

import com.astroverse.backend.service.TokenBlackListService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestInterceptor implements HandlerInterceptor {
    private final TokenBlackListService tokenBlackListService;
    private final JwtUtil jwtUtil;

    public RequestInterceptor(TokenBlackListService tokenBlackListService, JwtUtil jwtUtil) {
        this.tokenBlackListService = tokenBlackListService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!isValidRequest(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Richiesta non valida");
            return false;
        }
        return true;
    }

    private boolean isValidRequest(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            return false;
        }
        accessToken = accessToken.replace("Bearer ", "");
        if (tokenBlackListService.existToken(accessToken)) {
            return false;
        }
        if (!jwtUtil.isTokenValid(accessToken)) {
            System.out.println("accessToken: " + accessToken);
            return false;
        }
        return true;
    }
}