package com.app.servicesb.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalTokenFilter extends OncePerRequestFilter {

    @Value("${internal.token}")
    private String internalToken;

    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

    String requestToken = request.getHeader(INTERNAL_TOKEN_HEADER);

        // Перевіряємо, чи існує токен та чи він співпадає із секретним
        if (requestToken == null || !requestToken.equals(internalToken)) {

            // Якщо ні — повертаємо 403 Forbidden з поясненням
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("text/plain");
            response.getWriter().write("Access Denied: Invalid or missing internal token.");
            return;
        }

        // Якщо токен дійсний, продовжуємо виконання
        filterChain.doFilter(request, response);

    }
}
