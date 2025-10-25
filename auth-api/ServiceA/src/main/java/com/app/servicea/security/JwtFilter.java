package com.app.servicea.security;

import com.app.servicea.entity.Users;
import com.app.servicea.repository.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepository;

    public JwtFilter(JwtUtil jwtUtil, UsersRepository usersRepository) {
        this.jwtUtil = jwtUtil;
        this.usersRepository = usersRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Витягуємо заголовок Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final UUID userId;

        // 2. Перевіряємо формат заголовка (повинен починатися з "Bearer ")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;// Пропускаємо, якщо немає токена або він невірний (публічний доступ)
        }

        // 3. Витягуємо сам токен (відкидаємо "Bearer ")
        jwt = authHeader.substring(7);

        // 4. Валідація токена та витягнення ID користувача
        userId = UUID.fromString(jwt);

        // 5. Перевіряємо ID та відсутність поточної аутентифікації
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
// Завантажуємо дані користувача з бази (необхідно, оскільки ми не використовуємо UserDetailsService)
            Users user = usersRepository.findById(userId)
                    .orElse(null);

            // 6. Якщо користувача знайдено і токен дійсний (перевірено в extractUserId)
            if (user != null) {

                // 7. Створюємо об'єкт аутентифікації
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user, // Principal (об'єкт користувача)
                        null // Credentials (пароль не потрібен для токена)
//!!!!!!!!!!!!!!!!      user.getAuthorities() // Authorities (права користувача, якщо вони є)
                );

                // Додаємо деталі запиту
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 8. Встановлюємо об'єкт аутентифікації в контекст безпеки
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

    }
}
