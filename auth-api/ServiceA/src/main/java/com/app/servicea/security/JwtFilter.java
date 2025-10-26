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

    private static final String AUTH_PATH_PREFIX = "/api/auth/";

    public JwtFilter(JwtUtil jwtUtil, UsersRepository usersRepository) {
        this.jwtUtil = jwtUtil;
        this.usersRepository = usersRepository;
    }

    // Використовуємо shouldNotFilter для ігнорування публічних шляхів
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Якщо запит починається з /api/auth/, не застосовувати цей фільтр
        return request.getRequestURI().startsWith(AUTH_PATH_PREFIX);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Витягуємо заголовок Authorization
        final String authHeader = request.getHeader("Authorization");

        // 2. Якщо заголовок відсутній або не починається з "Bearer ", продовжуємо.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Витягуємо сам токен (відкидаємо "Bearer ")
        final String jwt = authHeader.substring(7);

        String userIdString = null;
        UUID userId = null;

        try {
            // *** ВИПРАВЛЕННЯ: Використовуємо JwtUtil для витягнення ID з токена. ***
            // Це замінює помилковий UUID.fromString(jwt)
            userIdString = String.valueOf(jwtUtil.extractUserId(jwt));

            // Якщо ID витягнуто, конвертуємо його в UUID.
            if (userIdString != null) {
                userId = UUID.fromString(userIdString);
            }
        } catch (Exception e) {
            // Помилка валідації/парсингу токена (наприклад, прострочений або невалідний підпис)
            System.err.println("JWT Token validation error: " + e.getMessage());
            // Продовжуємо, але без аутентифікації. Spring Security заблокує захищений шлях.
            filterChain.doFilter(request, response);
            return;
        }


        // 5. Перевіряємо ID та відсутність поточної аутентифікації
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Завантажуємо дані користувача з бази
            Users user = usersRepository.findById(userId).orElse(null);

            // 6. Якщо користувача знайдено
            if (user != null) {
                // 7. Створюємо об'єкт аутентифікації
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 8. Встановлюємо об'єкт аутентифікації в контекст безпеки
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
